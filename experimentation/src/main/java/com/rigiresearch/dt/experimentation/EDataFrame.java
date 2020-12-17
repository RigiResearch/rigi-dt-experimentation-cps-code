package com.rigiresearch.dt.experimentation;

import com.github.rcaller.datatypes.DataFrame;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;

/**
 * A DataFrame.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
@RequiredArgsConstructor
public final class EDataFrame {

    /**
     * Label associated with the group name in the dataframe.
     */
    public static final String GROUP = "group";

    /**
     * Label associated with observed values in the dataframe.
     */
    public static final String OBSERVATION = "observation";

    /**
     * The input data.
     */
    private final Map<String, Double[]> data;

    /**
     * Loads the data into a dataframe.
     * @return A non-null, possibly empty dataframe
     */
    public DataFrame dataframe() {
        final List<Entry<String, Double[]>> entries =
            new ArrayList<>(this.data.entrySet());
        final int records = entries.size() * entries.get(0).getValue().length;
        final Object[][] objects = new Object[2][records];
        for (int i = 0, j = 0; i < entries.size() && j < records; i++) {
            final Map.Entry<String, Double[]> entry = entries.get(i);
            for (int k = 0; k < entry.getValue().length; k++, j++) {
                objects[0][j] = entry.getKey();
                objects[1][j] = entry.getValue()[k];
            }
        }
        return DataFrame.create(
            objects,
            new String[]{EDataFrame.GROUP, EDataFrame.OBSERVATION}
        );
    }

}
