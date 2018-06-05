require(graphics)

#setwd("D:\\git\\QAP\\docs\\results-experiments")

source("wilcoxon.R")

average = read.csv("average.csv", header = TRUE)

average_nols = average[,2:3]
average_ls_not_tuned = average[,4:5]
average_ls_tuned = average[,6:7]

do.wilcoxon(data.matrix=average_nols, output="result-wilcoxon-nols.txt")
do.wilcoxon(data.matrix=average_ls_not_tuned, output="result-wilcoxon-ls-not-tuned.txt")
do.wilcoxon(data.matrix=average_ls_tuned, output="result-wilcoxon-ls-tuned.txt")
