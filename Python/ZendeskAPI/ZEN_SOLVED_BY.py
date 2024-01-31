import requests
import pyodbc
from datetime import datetime

# RETURNS AN INT ERROR BUT WORKS.

headers = {
    "Content-Type": "application/json",
}

auth = ("aidan.horne@capellaconsulting.co.nz/token", "XHUm0W0pXTa2Ma7kkWdAWw5hYiHElbg6Pu1nUCAO")

# Construct connection string
connection_string = connection_string = 'DRIVER={SQL Server};SERVER=CapDBServer;DATABASE=capella;UID=aidanh;PWD=Quack1nce4^'

# Establish connection
connection = pyodbc.connect(connection_string)
cursor = connection.cursor()

ticket_ids = []
current_val = "x"
# while len(current_val) != 0 or current_val is not None:
cursor.execute("SELECT TICKETID FROM ZEN_TICKETS")
all_ids = cursor.fetchall()
for i in range(len(all_ids)):
    all_ids[i] = all_ids[i][0]

url = f"https://capellasupport.zendesk.com/api/v2/tickets/{all_ids[0]}/audits"

def isInt(var):
    try:
        int(var)
        return var
    except TypeError:
        return None

def dictToString(var):
    result_string = ""
    for key, value in var.items():
        result_string += f"{key}: {value}, "
    return result_string

previous_event = None
count = 0
insert_data = []
# Create a session for more verbose output
while url is not None:
    with requests.Session() as session:
        session.headers.update(headers)
        session.auth = auth
        response = session.get(url)
        try:
            if response.status_code == 200:
                # Successful request
                data = response.json()
                for result in data.values():
                    if result is not None:
                        try:
                            for var in result:
                                for event in var.get("events"):
                                    if event.get("field_name") == "status" and event.get("value") == "solved":
                                        event_id = event.get("id")
                                        pid = var.get("author_id")
                                        previous_value = event.get("previous_value")
                                        value = event.get("value")
                                        insert_data = [var.get("ticket_id"), event_id, pid, event.get("value"), var.get("created_at")]
                            try:
                                cursor.execute("""
                                    INSERT INTO ZEN_TICKETS_SOLVED_BY ([TICKETID], [EVENTID], [PID], [STATUS], [CREATED_AT])
                                    VALUES (?, ?, ?, ?, ?)
                                """, insert_data)

                                connection.commit()
                            except pyodbc.IntegrityError:
                                pass    
                        except TypeError:
                            count += 1
                            url = f"https://capellasupport.zendesk.com/api/v2/tickets/{all_ids[count]}/audits"
                            print(f"Gathering information from ticket: {all_ids[count]}")

        except AttributeError:
            print(f"Skipping ticket {all_ids[count]}")
            count += 1
            url = f"https://capellasupport.zendesk.com/api/v2/tickets/{all_ids[count]}/audits"
            print(f"Gathering information from ticket: {all_ids[count]}")