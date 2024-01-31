from datetime import datetime, timezone

# Define the date
date_str = "2010-09-26"

# Convert the date to a datetime object
date_object = datetime.strptime(date_str, "%Y-%m-%d")

# Calculate the epoch time
epoch_time = int(date_object.replace(tzinfo=timezone.utc).timestamp())

print(f"The epoch time for {date_str} is: {epoch_time}")
