######################################################
# 1. Set up packages and libraries
# 2. Setup global variables
# 3. Charts
######################################################

if (!require("devtools")) install.packages("devtools")
devtools::install_github("mkuhn/dict")
devtools::install_github('bbc/bbplot')

if (!require("lubridate")) install.packages("lubridate")
if (!require("dplyr")) install.packages("dplyr")
if (!require("ggplot2")) install.packages("ggplot2")
if (!require("ggExtra")) install.packages("ggExtra")
if (!require("grid")) install.packages("grid")
if (!require("gridExtra")) install.packages("gridExtra")

library(lubridate)
library(dplyr)
library(ggplot2)
library(bbplot)
library(ggExtra)
library(grid)
library(dict)

################################################
# 2. Setup global variables
################################################

# Replace the original function to save the plots
source(sprintf("%s/finalise_plot.R",getwd()),local=TRUE)

# Set the default ggplot2 theme
theme_set(bbc_style())

height = 220
width = 400
dir = "charts"
colors <- c("#1F77B4", "#FF7F0E", "#AEC7E8", "#FFBB78", "#2CA02C",
            "#98DF8A", "#D62728", "#FF9896", "#9467BD", "#C5B0D5",
            "#8C564B", "#E377C2", "#7F7F7F", "#BCBD22", "#17BECF")
custom_style <- bbc_style() + # theme_minimal()
  theme(legend.position="bottom") +
  theme(panel.grid.major.y=element_line(color="#eeeeee")) +
  theme(plot.title=element_text(size=18,color="#063376",hjust=0)) +
  theme(plot.subtitle=element_text(size=12,margin=margin(6, 0, 8, 0),hjust=0)) +
  theme(legend.text=element_text(size=12)) +
  theme(axis.text=element_text(size=11)) +
  theme(axis.text.x=element_text(size=9,color="#666666"))

results = read.csv("../sim-results.csv")
min_seq = min(results$number)
max_seq = max(results$number)

# Filtered data by result type
T31n <- results[grep("T31n",results$line),]
T31s <- results[grep("T31s",results$line),]

################################################
# 3. Charts
################################################

##
## CH1. Fitness progression over time
##

miniature_title = "Overall Fitness Performance"
miniature_subtitle = "Fitness progression over (simulated) time"

fitness_chart <- ggplot(results,aes(x=number,y=simulation.fitness)) +
  geom_line(size=1) +
  custom_style +
  theme(legend.position="none") +
  # Legend
  scale_color_manual(values=colors) +
  # Labels
  labs(title=miniature_title,subtitle=miniature_subtitle) +
  # Axis
  scale_y_continuous("Fitness value") +
  scale_x_continuous("Chromosome sequence")

finalise_plot(plot_name = fitness_chart,
              save_filepath = sprintf("%s/overall_fitness_performance.pdf", dir),
              width_pixels = width,
              height_pixels = height)
