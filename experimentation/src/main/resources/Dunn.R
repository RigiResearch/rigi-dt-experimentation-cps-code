install.packages("dunn.test")
library("dunn.test")

# Structure is:
# group observation
# group1 0.5
# group2 0.6
# ...
x <- c( "Calvin", "Chris", "Raj")
y <- c( 10, 25, 19)
df <- data.frame("group"=x,"observation"=y)

attach(df)

# Suggested method. Read more: https://stats.stackexchange.com/a/71491
result <- dunn.test(observation, group, method="hochberg", list=TRUE)
# - Print the different groups being compared
comparisons <- result$comparisons
# - Adjusted p values
values <- result$P.adjust
# - Difference in means
difference <- result$Z
