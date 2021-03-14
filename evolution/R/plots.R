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
if (!require("plotly")) install.packages("plotly")

library(lubridate)
library(dplyr)
library(ggplot2)
library(bbplot)
library(ggExtra)
library(grid)
library(dict)
library(plotly)

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

results = read.csv("results.csv")
# Remove column "line"
results = subset(results, select=-c(line))
min_seq = min(results$number)
max_seq = max(results$number)

# Average replica data by simulation number (sim. number is unique)
# ewt.a = aggregate(ewt.a ~ number, results, mean)
results.averaged <- results %>% group_by(number) %>% summarise_each(funs(mean))
results.averaged = subset(results.averaged, select=-c(replica))
results.averaged$line = "T31s"

T31s <- results.averaged[grep("T31s",results.averaged$line),]

################################################
# 3. Charts
################################################

##
## CH1. Fitness progression over time
##

miniature_title = "Overall Fitness Performance"
miniature_subtitle = "Fitness progression over (simulated) time"

fitness_chart <- ggplot(results.averaged,aes(x=number,y=simulation.fitness)) +
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

##
## CH2. Excess waiting time (vs headway design)
##

miniature_title = "Excess Waiting Time"
miniature_subtitle = "Effect of headway design on passenger waiting time"

headway_ewt_chart <- ggplot(results.averaged,aes(x=headway,y=ewt.a)) +
  geom_point(aes(color = factor(line))) +
  custom_style +
  theme(legend.position="none") +
  # Legend
  scale_color_manual(values=colors) +
  # Labels
  labs(title=miniature_title,subtitle=miniature_subtitle) +
  # Axis
  scale_y_continuous("Excess waiting time") +
  scale_x_continuous("Headway design")

finalise_plot(plot_name = headway_ewt_chart,
              save_filepath = sprintf("%s/headway_ewt.pdf", dir),
              width_pixels = width,
              height_pixels = height)

##
## CH3. Excess waiting time (vs operating fleet size)
##

miniature_title = "Excess Waiting Time"
miniature_subtitle = "Effect of operating fleet size on passenger waiting time"

fleet_ewt_chart <- ggplot(results.averaged,aes(x=buses,y=ewt.a)) +
  geom_point(aes(color = factor(line))) +
  custom_style +
  theme(legend.position="none") +
  # Legend
  scale_color_manual(values=colors) +
  # Labels
  labs(title=miniature_title,subtitle=miniature_subtitle) +
  # Axis
  scale_y_continuous("Excess waiting time") +
  scale_x_continuous("Number of buses")

finalise_plot(plot_name = fleet_ewt_chart,
              save_filepath = sprintf("%s/fleet_ewt.pdf", dir),
              width_pixels = width,
              height_pixels = height)


##
## CH4. Excess waiting time (vs operating fleet size)
##

miniature_title = "Excess Waiting Time"
miniature_subtitle = "Effect of operating fleet size and headway design on passenger waiting time"

axx <- list(title = "Fleet size")
axy <- list(title = "Headway")
axz <- list(title = "EWT")

# Separate plots
ewt <- plot_ly(
  x = results.averaged$buses,
  y = results.averaged$headway,
  z = results.averaged$ewt.a,
  type= "scatter3d",
  mode = "markers",
  color = results.averaged$line
)
ewt <- ewt %>% layout(
  scene = list(xaxis=axx,yaxis=axy,zaxis=axz,aspectmode='cube'),
  title = miniature_title
)
ewt

ewt.T31s <- plot_ly(z = ~rbind(T31s$buses,T31s$headway,T31s$ewt.a))
ewt.T31s <- ewt.T31s %>% add_surface()
ewt.T31s <- ewt.T31s %>% layout(
  scene = list(xaxis=axx,yaxis=axy,zaxis=axz,aspectmode='cube'),
  title = miniature_title
)
ewt.T31s
