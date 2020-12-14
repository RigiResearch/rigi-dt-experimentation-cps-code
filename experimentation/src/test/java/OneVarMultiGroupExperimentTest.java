import com.rigiresearch.dt.experimentation.Experiment;
import com.rigiresearch.dt.experimentation.OneVarMultiGroupExperiment;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests {@link OneVarMultiGroupExperiment}.
 * @author Miguel Jimenez (miguel@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
class OneVarMultiGroupExperimentTest {

    /**
     * The logger.
     */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(OneVarMultiGroupExperimentTest.class);

    @Test
    void dummyData() {
        final Double[] treatment = {63.5, 81.3, 88.9, 63.5, 76.2};
        final Double[] control = {66.0, 64.8, 74.9, 81.3, 76.2};
        final Map<String, Double[]> samples = new HashMap<>(2);
        samples.put("treatment", treatment);
        samples.put("control", control);
        final double alpha = 0.05;
        final Experiment experiment = new OneVarMultiGroupExperiment(samples, alpha);
        OneVarMultiGroupExperimentTest.LOGGER.info("\n{}", experiment.result());
    }

}
