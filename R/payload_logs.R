install.packages("tidyverse")
install.packages("dplyr")

library("tidyverse")
library("dplyr")

x = read.delim("C:\\Users\\AidanHorne\\Desktop\\Programming\\R\\Control_agent_payload_logs.txt")

x_table = as_tibble(x)

test_row = head(x_table, 1)

test_row
date = substr(test_row, 1, 10)
date
time = substr(test_row, 11, 19)
time
type = substr(test_row, 46, nchar(test_row))
type
