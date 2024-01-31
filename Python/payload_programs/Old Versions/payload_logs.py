import pandas as pd
import numpy as np
import csv

def writeColumnsToCSV(clist):
    clist = list(dict.fromkeys(clist))
    clist_dataDict = dict()

    # Removing empty character from column list
    del clist[3]
    for i in range(len(valuableData)):
        for j in range(1, len(valuableData[i]) - 1):
            if valuableData[i][j] in clist and valuableData[i][j] not in clist_dataDict.keys() and valuableData[i][j] not in clist_dataDict.values():
                clist_dataDict[valuableData[i][j]] = valuableData[i][j + 1]

    results = []
    for value in clist_dataDict.items():
        results.append("Key = {} Value = {}\n".format(value[0], value[1]))

    with open("payload_logs_result.csv", 'w', newline='') as file:
        writer = csv.writer(file)

        for value in clist_dataDict.items():
            writer.writerow([value[0], value[1]])


f = open("Control_agent_payload_logs.txt")
data = f.readlines()
valuableData = []
reqIdData = []
scrubbed_data = []
date_data = []

for d in data:
    if len(d) > 2:
        scrubbed_data.append(d)

# Adding the first two column headers
clist = ["Date", "Time"]

# This removes "<", ">", "/" and duplicates to clean the data
for i in range(len(scrubbed_data) - 1):
    if "Uploading message:" in scrubbed_data[i]:
        temp = scrubbed_data[i].rfind("UTF-8") + 9
        temp = scrubbed_data[i][temp:]
        temp = temp.replace("/", "")

        # Adding variable names to a column list.
        tempstring = ""
        brac = False
        for x in range(len(temp)):
            if temp[x] == "<" or brac == True:
                if temp[x] != "<":
                    tempstring = tempstring + temp[x]
                brac = True
            if temp[x] == ">":
                tempstring = tempstring.replace(">", "")
                clist.append(tempstring)
                tempstring = ""
                brac = False

        # Getting rid of duplicates
        temp = temp.replace("<", " ")
        temp = temp.replace(">", " ")
        temp = temp.split()
        temp = list(dict.fromkeys(temp))

        temp = " ".join(temp)
        key = ""
        date_data.append(scrubbed_data[i].split()[0])
        date_data.append(scrubbed_data[i].split()[1])
        valuableData.append(temp.split())
    else:
        pass
        # reqIdData.append("{} {} {} {}".format(scrubbed_data[i].split()[0], scrubbed_data[i].split()[1], scrubbed_data[i].split()[6], scrubbed_data[i].split()[7]))

writeColumnsToCSV(clist)

