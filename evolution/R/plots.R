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
if (!require("ggpubr")) install.packages("ggpubr")
if (!require("grid")) install.packages("grid")
if (!require("gridExtra")) install.packages("gridExtra")
if (!require("plotly")) install.packages("plotly")

library(lubridate)
library(dplyr)
library(ggplot2)
library(bbplot)
library(ggExtra)
library(ggpubr)
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
custom_style <- theme_minimal() + # bbc_style()
  theme(legend.position="bottom") +
  theme(panel.grid.major.y=element_line(color="#eeeeee")) +
  theme(plot.title=element_text(size=18,color="#063376",hjust=0)) +
  theme(plot.subtitle=element_text(size=12,margin=margin(6, 0, 8, 0),hjust=0)) +
  theme(legend.text=element_text(size=12)) +
  theme(axis.text=element_text(size=11)) +
  theme(axis.text.x=element_text(size=9,color="#666666"))

results = read.csv("results.csv")
fitness = read.csv("fitness-results.csv")
# results = read.csv("../sim-results.csv")
# fitness = read.csv("../fitness-results.csv")

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

write.csv(results.averaged,'averaged-results.csv',row.names=FALSE)

################################################
# 3. Charts
################################################

##
## CH1. Fitness progression over time
##

fitness_title = "Overall Fitness Performance"
fitness_title = NULL
fitness_chart <- ggplot(fitness,aes(x=generation,y=fitness)) +
  geom_line(size=1) +
  geom_smooth(method='lm',aes(color="#BADA55")) +
  stat_cor(method = "pearson") +
  custom_style +
  theme(legend.position="none") +
  # Legend
  scale_color_manual(values=colors) +
  # Labels
  labs(title=fitness_title) +
  # Axis
  scale_y_continuous("Fitness value") +
  scale_x_continuous("Generation (Best chromosome)")

finalise_plot(plot_name = fitness_chart,
              save_filepath = sprintf("%s/overall_fitness_performance.pdf", dir),
              width_pixels = width,
              height_pixels = height)

##
## CH2. Excess waiting time (vs headway design)
##

headway_ewt_title = "Headway design vs EWT"
headway_ewt_title = NULL
headway_ewt_chart <- ggplot(results.averaged,aes(x=headway,y=ewt.a)) +
  geom_point(aes(color = factor(line))) +
  geom_smooth(method='lm',aes(color="#BADA55")) +
  stat_cor(method = "pearson") +
  custom_style +
  theme(legend.position="none") +
  # Legend
  scale_color_manual(values=colors) +
  # Labels
  labs(title=headway_ewt_title) +
  # Axis
  scale_y_continuous("Excess waiting time") +
  scale_x_continuous("Headway design")

finalise_plot(plot_name = headway_ewt_chart,
              save_filepath = sprintf("%s/headway_ewt.pdf", dir),
              width_pixels = width,
              height_pixels = height)

##
## CH3. Interpolated function
##

spline.d <- as.data.frame(spline(results.averaged$headway, results.averaged$ewt.a))

headway_ewt_int_title = "Headway design vs EWT (interpolation)"
headway_ewt_int_title = NULL
headway_ewt_int_chart <- ggplot(results.averaged,aes(x=headway,y=ewt.a)) +
  geom_point(aes(color = factor(line))) +
  geom_line(data=spline.d, aes(x=x,y=y)) +
  # stat_cor(method = "pearson") +
  custom_style +
  theme(legend.position="none") +
  # Legend
  scale_color_manual(values=colors) +
  # Labels
  labs(title=headway_ewt_int_title) +
  # Axis
  scale_y_continuous("Excess waiting time") +
  scale_x_continuous("Headway design")

finalise_plot(plot_name = headway_ewt_int_chart,
              save_filepath = sprintf("%s/headway_ewt_int.pdf", dir),
              width_pixels = width,
              height_pixels = height)


approximated <- function (x) {
  sqrt(9.0 + (((7.0 + 2.0*x) + (x + sqrt(6.0 + ((8.0 + 2.0*x) + ((4.0 + 2.0*x) + (x + ((4.0 + 2.0*x) + 9.0))))))) + (6.0 + ((9.0 + (x + ((6.0 + 2.0*x) + 6.0))) + 2.0*x))))
}

headway_ewt_appr_title = "Approximated function using Symbolic Regression"
headway_ewt_appr_title = NULL
headway_ewt_appr_chart <- ggplot(results.averaged,aes(x=headway,y=ewt.a)) +
  geom_point(aes(color = factor(line))) +
  stat_function(fun=approximated,aes(x=headway)) +
  custom_style +
  theme(legend.position="none") +
  # Legend
  scale_color_manual(values=colors) +
  # Labels
  labs(title=headway_ewt_appr_title) +
  # Axis
  scale_y_continuous("Excess waiting time") +
  ylim(-200, 120) +
  scale_x_continuous("Headway design")

finalise_plot(plot_name = headway_ewt_appr_chart,
              save_filepath = sprintf("%s/headway_ewt_appr.pdf", dir),
              width_pixels = width,
              height_pixels = height)

##
## CH4. Excess waiting time (vs headway coefficient of variation)
##

hcv_ewt_title = "HCoV vs EWT"
hcv_ewt_title = NULL
hcv_ewt_chart <- ggplot(results.averaged,aes(x=hcv,y=ewt.a)) +
  geom_point(aes(color = factor(line))) +
  geom_smooth(method='lm',aes(color="#BADA55")) +
  stat_cor(method = "pearson") +
  custom_style +
  theme(legend.position="none") +
  # Legend
  scale_color_manual(values=colors) +
  # Labels
  labs(title=hcv_ewt_title) +
  # Axis
  scale_y_continuous("Excess waiting time") +
  scale_x_continuous("Headway coefficient of variation")

finalise_plot(plot_name = hcv_ewt_chart,
              save_filepath = sprintf("%s/hcv_ewt.pdf", dir),
              width_pixels = width,
              height_pixels = height)


##
## CH5. Excess waiting time (vs headway coefficient of variation)
##

hcv_headway_title = "Headway vs HCoV"
hcv_headway_title = NULL
hcv_headway_chart <- ggplot(results.averaged,aes(x=headway,y=hcv)) +
  geom_point(aes(color = factor(line))) +
  geom_smooth(method='lm',aes(color="#BADA55")) +
  stat_cor(method = "pearson") +
  custom_style +
  theme(legend.position="none") +
  # Legend
  scale_color_manual(values=colors) +
  # Labels
  labs(title=hcv_headway_title) +
  # Axis
  scale_y_continuous("Excess waiting time") +
  scale_x_continuous("Headway coefficient of variation")

finalise_plot(plot_name = hcv_headway_chart,
              save_filepath = sprintf("%s/hcv_headway.pdf", dir),
              width_pixels = width,
              height_pixels = height)
##
## CH6. Excess waiting time (vs operating fleet size)
##

fleet_ewt_title = "Operating fleet size vs EWT"
fleet_ewt_title = NULL
fleet_ewt_chart <- ggplot(results.averaged,aes(x=buses,y=ewt.a)) +
  geom_point(aes(color = factor(line))) +
  geom_smooth(method='lm',aes(color="#BADA55")) +
  stat_cor(method = "pearson") +
  custom_style +
  theme(legend.position="none") +
  # Legend
  scale_color_manual(values=colors) +
  # Labels
  labs(title=fleet_ewt_title) +
  # Axis
  scale_y_continuous("Excess waiting time") +
  scale_x_continuous("Number of buses")

finalise_plot(plot_name = fleet_ewt_chart,
              save_filepath = sprintf("%s/fleet_ewt.pdf", dir),
              width_pixels = width,
              height_pixels = height)

##
## CH7. Excess waiting time (vs operating fleet size)
##

# miniature_title = "EWT vs Headway and Fleet size"
# 
# axx <- list(title = "Fleet size")
# axy <- list(title = "Headway")
# axz <- list(title = "EWT")
# 
# # Separate plots
# ewt <- plot_ly(
#   x = results.averaged$buses,
#   y = results.averaged$headway,
#   z = results.averaged$ewt.a,
#   type= "scatter3d",
#   mode = "markers",
#   color = results.averaged$line
# )
# ewt <- ewt %>% layout(
#   scene = list(xaxis=axx,yaxis=axy,zaxis=axz,aspectmode='cube'),
#   title = miniature_title
# )
# ewt
# 
# ewt.T31s <- plot_ly(z = ~rbind(T31s$buses,T31s$headway,T31s$ewt.a))
# ewt.T31s <- ewt.T31s %>% add_surface()
# ewt.T31s <- ewt.T31s %>% layout(
#   scene = list(xaxis=axx,yaxis=axy,zaxis=axz,aspectmode='cube'),
#   title = miniature_title
# )
# ewt.T31s
# 

