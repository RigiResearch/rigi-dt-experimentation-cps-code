# -*- coding: utf-8 -*-
"""
Created on Thu Mar 11 19:29:41 2021

@author: lfeli
"""

import pandas as pd

file = 'output-02-APR-19-awk-sorted.csv'
date_format_csv = '%Y-%m-%d %H:%M:%S'
date_format_plot = '%Y-%m-%d %H:%M'

# lineId (T31) = 131
lines = [131]

# date boundaries
lower_boundary = '2019-04-02 05:00'
upper_boundary = '2019-04-02 11:00'


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

df = pd.read_csv(file,sep=',',names=headers,dtype=data_types)
df_filtered_by_line = df[(df['lineId'].isin(lines))]
df_adjusted = df_filtered_by_line[['datagramDate','busId','stopId','odometer','longitude','latitude','taskId','lineId','tripId','event_type']]
#String to Date
df_adjusted['datagramDate']= pd.to_datetime(df_adjusted['datagramDate'])
#Date to String (Reformat)
df_adjusted['datagramDate'] = df_adjusted['datagramDate'].dt.strftime(date_format_csv)

df_buses_range = df_adjusted['busId'].drop_duplicates()


df_filtered_by_datetime = df_adjusted[(df_adjusted['datagramDate']>lower_boundary) & (df_adjusted['datagramDate']<upper_boundary)]

# histogram
#df_adjusted['datagramDate'] = df_adjusted['datagramDate'].dt.strftime(date_format_plot)
#df_grouped_by_date = df_adjusted.groupby(['datagramDate']).size().reset_index(name='counts')
#df_grouped_by_date['datagramDate'] = pd.to_datetime(df_grouped_by_date['datagramDate'])

#df_grouped_by_date.to_csv('T31-APR-2.csv',index=False)

#df_grouped_by_date.plot.bar(x='datagramDate', y='counts', rot=90)

df_filtered_by_datetime.to_csv("datagrams_generated.csv",index=False)


