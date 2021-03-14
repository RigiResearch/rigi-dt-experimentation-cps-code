# -*- coding: utf-8 -*-
"""
Created on Sat Mar 13 14:12:32 2021

@author: lfeli
"""

import pandas as pd
import os
from datetime import date

os.chdir("../resources")
path = os.getcwd()

date_format_csv = '%H:%M:%S'
date_format_today = '%Y-%m-%d'

today = str(date.today().strftime(date_format_today))

# date boundaries
lower_boundary = today + ' ' + '05:00'
upper_boundary = today + ' ' + '11:00'

files = ['output-02-APR-19-awk-sorted.csv','output-09-APR-19-awk-sorted.csv','output-23-APR-19-awk-sorted.csv','output-30-APR-19-awk-sorted.csv']

# lineId (T31) = 131
lines = [131]

headers = ['event_type','short_datagram_date','stopId','odometer','latitude','longitude','taskId','lineId','tripId','unkown1','datagramDate','busId']

data_types = {'event_type': int,
              'short_datagram_date' : str,
              'stopId' : int,
              'odometer' : int,
              'latitude' : int,
              'longitude' : int,
              'taskId' : int,
              'lineId' : int,
              'tripId' : int,
              'unkown1' : int,
              'datagramDate' : str,
              'busId' : int}

df_02 = pd.read_csv(files[0],sep=',',names=headers,dtype=data_types)
df_02 = df_02[(df_02['lineId'].isin(lines))]

df_19 = pd.read_csv(files[1],sep=',',names=headers,dtype=data_types)
df_19 = df_19[(df_19['lineId'].isin(lines))]

df_23 = pd.read_csv(files[2],sep=',',names=headers,dtype=data_types)
df_23 = df_23[(df_23['lineId'].isin(lines))]

df_30 = pd.read_csv(files[3],sep=',',names=headers,dtype=data_types)
df_30 = df_30[(df_30['lineId'].isin(lines))]

dfs = [df_02,df_19,df_23,df_30]

aggregate = pd.concat(dfs)

aggregate_adjusted = aggregate[['datagramDate','busId','stopId','odometer','longitude','latitude','taskId','lineId','tripId','event_type']]

# String to date
aggregate_adjusted['datagramDate']= pd.to_datetime(aggregate_adjusted['datagramDate'])

#Date to String (Reformat)
aggregate_adjusted['datagramDate'] = aggregate_adjusted['datagramDate'].dt.strftime(date_format_csv)

aggregate_sorted = aggregate_adjusted.sort_values(['datagramDate'], ascending = [True])

aggregate_sorted['datagramDate'] = (today + ' ') + aggregate_sorted['datagramDate'].astype(str)

aggregate_sorted['datagramDate'] = pd.to_datetime(aggregate_sorted['datagramDate'])

aggregate_sorted_filtered = aggregate_sorted[(aggregate_sorted['datagramDate']>lower_boundary) & (aggregate_sorted['datagramDate']<upper_boundary)]

aggregate_sorted_filtered.to_csv("datagrams_aggregated_generated.csv",index=False)