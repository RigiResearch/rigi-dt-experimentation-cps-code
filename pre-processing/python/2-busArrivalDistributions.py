# -*- coding: utf-8 -*-
"""
Created on Thu Mar 10 01:32:32 2021

@author: lfrivera
"""

from enum import Enum
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from sklearn.preprocessing import StandardScaler
import scipy
import scipy.stats
import sys
#%matplotlib inline
#import warnings

#warnings.filterwarnings("ignore")

# The chi-squared value bins data into 50 bins (this could be reduced for smaller data sets) based on percentiles so that each bin contains approximately an equal number of values
bins = 5

#Distributions to test (qw)
dist_names = ['beta',
              'expon',
              'gamma',
              'lognorm',
              'loglaplace',
              'pearson3',
              'triang',
              'uniform',
              'weibull_min', 
              'weibull_max']

def list_parameters(distribution):
    """List parameters for scipy.stats.distribution.
    # Arguments
        distribution: a string or scipy.stats distribution object.
    # Returns
        A list of distribution parameter strings.
    """
    if isinstance(distribution, str):
        distribution = getattr(scipy.stats, distribution)
    if distribution.shapes:
        parameters = [name.strip() for name in distribution.shapes.split(',')]
    else:
        parameters = []
    if distribution.name in scipy.stats._discrete_distns._distn_names:
        parameters += ['loc']
    elif distribution.name in scipy.stats._continuous_distns._distn_names:
        parameters += ['loc', 'scale']
    else:
        sys.exit("Distribution name not found in discrete or continuous lists.")
    return parameters

# StopIds
class Stop(Enum):
    Chiminangos_A2 = 500200
 #   Salomia_A = 500300
 #   Salomia_B = 500301
 #   Popular_A = 500350
 #   Popular_B = 500353

# Load data 
dataframe = pd.read_csv('interarrivalTimes.csv')

for stop in Stop:
    print("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
    stopId = stop.value 
    # filter data by stop id
    filtered = dataframe[(dataframe.stop_id==stopId)]
    # get interarrival times
    interarrival_times = filtered[["Ai"]]
    # transform to array
    ais = interarrival_times.values
    
    # create an index array (x) for data
    x = np.arange(len(ais))
    size = len(ais)
    
    # descriptions and visualization of Ais
    print("|StopID: %d - %s|" % (stopId,Stop(stopId)))
    print(interarrival_times.describe())
    plt.hist(ais)
    plt.show()
    
    # standardise the data
    sc=StandardScaler() 
    temp = ais.reshape(-1,1)
    sc.fit(temp)
    ais_std =sc.transform(temp)
    ais_std = ais_std.flatten()
    print("----------------Standardized Data (Ai)----------------")
    print(ais_std)
    del temp
    
    # empty lists to sore results
    chi_square = []
    p_values = []
    
    # set up bins for chi-square test
    # observed data will be approximately evenly distributed aross all bins
    percentile_bins = np.linspace(0,100,bins)
    percentile_cutoffs = np.percentile(ais_std, percentile_bins)
    observed_frequency, bins = (np.histogram(ais_std, bins=percentile_cutoffs))
    cum_observed_frequency = np.cumsum(observed_frequency)

    # navigate through candidate distributions
    for distribution in dist_names:
         # set up distribution and get fitted distribution parameters
         dist = getattr(scipy.stats, distribution)
         param = dist.fit(ais_std)
         
         # obtain the KS test P statistic, round it to 5 decimal places
         p = scipy.stats.kstest(ais_std, distribution, args=param)[1]
         p = np.around(p, 5)
         p_values.append(p)
         
         # get expected counts in percentile bins
         # this is based on a 'cumulative distrubution function' (cdf)
         cdf_fitted = dist.cdf(percentile_cutoffs, *param[:-2], loc=param[-2],scale=param[-1])
         
         expected_frequency = []
         for bin in range(len(percentile_bins)-1):
             expected_cdf_area = cdf_fitted[bin+1] - cdf_fitted[bin]
             expected_frequency.append(expected_cdf_area)
             
         # calculate chi-squared
         expected_frequency = np.array(expected_frequency) * size
         cum_expected_frequency = np.cumsum(expected_frequency)
         ss = sum (((cum_expected_frequency - cum_observed_frequency) ** 2) / cum_observed_frequency)
         chi_square.append(ss)
    
    # aggregate results and sort by goodness of fit (best at top)
    results = pd.DataFrame()
    results['Distribution'] = dist_names
    results['chi_square'] = chi_square
    results['p_value'] = p_values
    results.sort_values(['chi_square'], inplace=True)
    
    # report results
    print("----------------Distributions Sorted by Goodness of Fit----------------")
    print (results)
    
    # divide the observed data into 100 bins for plotting (this can be changed)
    number_of_bins = 100
    bin_cutoffs = np.linspace(np.percentile(ais,0), np.percentile(ais,99),number_of_bins)

    # create the plot
    h = plt.hist(ais, bins = bin_cutoffs, color='0.75')

    # get the top three distributions from the previous phase
    number_distributions_to_plot = 2
    dist_names = results['Distribution'].iloc[0:number_distributions_to_plot]

    # create an empty list to store fitted distribution parameters
    parameters = []
    labels = []
    

    # navigate through the distributions ot get line fit and parameters
    for dist_name in dist_names:
        # set up distribution and store distribution parameters
        dist = getattr(scipy.stats, dist_name)
        param = dist.fit(ais)
        labels.append(list_parameters(dist_name))
        parameters.append(param)
           
        #parameters = [dict(zip(labels, datum)) for datum in param]

        
        
        # Get line for each distribution (and scale to match observed data)
        pdf_fitted = dist.pdf(x, *param[:-2], loc=param[-2], scale=param[-1])
        scale_pdf = np.trapz (h[0], h[1][:-1]) / np.trapz (pdf_fitted, x)
        pdf_fitted *= scale_pdf
    
        # add the line to the plot
        plt.plot(pdf_fitted, label=dist_name)
    
        # set the plot x axis to contain 99% of the data
        # this can be removed, but sometimes outlier data makes the plot less clear
        plt.xlim(0,np.percentile(ais,99))

    # Add legend and display plot
    #plt.legend()
    #plt.show()

    # Store distribution paraemters in a dataframe
    dist_parameters = pd.DataFrame()
    dist_parameters['Distribution'] = (results['Distribution'].iloc[0:number_distributions_to_plot])
    dist_parameters['Distribution parameters labels'] = labels
    dist_parameters['Distribution parameters'] = parameters

    # Print parameter results
    print("----------------Distribution parameters:----------------")
    for index, row in dist_parameters.iterrows():
        print ("Distribution:", row[0])
        print ("Parameter Labels:", row[1])
        print ("Parameters:", row[2])

