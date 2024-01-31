import pandas as pd

# Load the CSV files into pandas DataFrames
df1 = pd.read_csv('C:/Users/AidanHorne/Downloads/file1.csv')
df2 = pd.read_csv('C:/Users/AidanHorne/Downloads/file2.csv')

# Concatenate the DataFrames along the rows
merged_df = pd.concat([df1, df2], ignore_index=True)

# Save the concatenated DataFrame to a new CSV file
merged_df.to_csv('merged_file.csv', index=False)
