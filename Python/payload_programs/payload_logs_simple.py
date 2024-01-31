import pandas as pd
import numpy as np
import re
import os

# Sort the data to be added to lists.
f = open(f"{os.getcwd()}/Control_agent_payload_logs.txt", encoding="utf-8")
if not os.path.exists("Log Results"):
    os.mkdir("Log Results")
blankLineData = f.readlines()
noBlankLineData = []

for i in range(len(blankLineData)):
    if len(blankLineData[i]) > 2:
        noBlankLineData.append(blankLineData[i])

dataSplit = []
otherData = []
headers = []

# Adding to two lists, one for the keys (headers) one for the values (dataSplit).
for item in noBlankLineData:
    if "Type=" in item:
        headers.append(re.split('[<>/]', item)[0].split()[5])
        cleanData = list(dict.fromkeys(filter(None, re.split('[<>/]', item))))[3:-1]
        dataSplit.append(item.split()[0:2] + cleanData)
    else:
        otherData.append(list(dict.fromkeys(filter(None, re.split('[<>/]', item)))))


columnList = []
columnName = ""

# Removing the "Type="
for i in range(len(headers)):
    if "=" in headers[i]:
        headers[i] = headers[i].split("=")[1]

# Adding lists to dictonary for simplicity and efficiency.
results = dict.fromkeys(headers, dataSplit)
results["Other"] = []

for item in otherData:
    results["Other"].append(item)

for item in results.keys():
    file = open(f"Log Results/{item}.txt", 'w', encoding="utf-8")

    for value in results.get(item):
        if item == "Other":
            file.write(" ".join(value) + "\n")
        if len(value) > 2:
            file.write(" ".join(value) + "\n" + "\n")

    file.close()