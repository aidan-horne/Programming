import time
import pandas as pd
import numpy as np
import csv
import re

# Sort the data to be added to lists.
f = open("Control_agent_payload_logs.txt")
blankLineData = f.readlines()
noBlankLineData = []

for i in range(len(blankLineData)):
    if len(blankLineData[i]) > 2:
        noBlankLineData.append(blankLineData[i])

dataSplit = []
headers = []

# Adding to two lists, one for the keys (headers) one for the values (dataSplit).
for item in noBlankLineData:
    if "Type=" in item:
        headers.append(re.split('[<>/]', item)[0].split()[5])
        dataSplit.append(list(dict.fromkeys(filter(None, re.split('[<>/]', item))))[3:-1])


columnList = []
columnName = ""
open = False

# Removing the "Type="
for i in range(len(headers)):
    headers[i] = headers[i].split("=")[1]

# Adding lists to dictonary for simplicity and efficiency.
results = dict.fromkeys(headers, dataSplit)

# Using a list of pandas dataframes for each type.
# allTables = dict.fromkeys(headers, pd.DataFrame())

switch = True

for key, values in results.items():
    for item in values:
        

