# -*- coding: utf-8 -*-
"""
Created on Thu Mar 11 10:18:06 2021

@author: lfeli
"""

import pandas as pd

# Orientation: 0 = South -> North ; 1 = North -> South
orientation = 0

dataframe_linestops = pd.read_csv('linestops.csv',sep=';')
filtered = dataframe_linestops[(dataframe_linestops['LINEID']==131) & (dataframe_linestops['ORIENTATION']==orientation)]
sorted_by_planv = filtered.sort_values(['PLANVERSIONID'], ascending = [False])
latest_plan_version = sorted_by_planv['PLANVERSIONID'].iloc[0]
only_plan_version = sorted_by_planv[(dataframe_linestops['PLANVERSIONID']==latest_plan_version)]
sorted_by_stopid = only_plan_version.sort_values(['STOPID'], ascending = [True])
distinct = sorted_by_stopid.groupby('STOPID').first().reset_index()
sorted_by_seq = distinct.sort_values(['STOPSEQUENCE'], ascending = [True])

dataframe_stops = pd.read_csv('stops.csv',sep=';',encoding = "ISO-8859-1")
stop_names = dataframe_stops[['STOPID','LONGNAME']]
stop_names_sorted_by_id = stop_names.sort_values(['STOPID'], ascending = [True])
distinct_stop_names = stop_names_sorted_by_id.groupby('STOPID').first().reset_index()
df_merge_col = pd.merge(sorted_by_seq, distinct_stop_names, on='STOPID')

#temp = only_plan_version.sort_values(['STOPSEQUENCE'], ascending = [True])