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

url = f"https://capellasupport.zendesk.com/api/v2/tickets/{all_ids[0]}/comments"

current_ids = []
pageCount = 24000

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
                                ticketId = all_ids[pageCount]
                                personId = var.get("author_id")
                                commentDate = var.get("created_at")
                                commentId = var.get("id")
                                isPublic = var.get("public")

                                if commentId in current_ids:
                                    pageCount += 1
                                    url = f"https://capellasupport.zendesk.com/api/v2/tickets/{all_ids[pageCount]}/comments"
                                current_ids.append(commentId) 

                                if commentDate is not None:
                                    dateCObject = datetime.strptime(commentDate, "%Y-%m-%dT%H:%M:%SZ")
                                else:
                                    dateCObject = None

                                data = [ticketId, personId, dateCObject, commentId, isPublic]

                                try:
                                    cursor.execute("""
                                        INSERT INTO ZEN_COMMENTS ([TICKETID], [PERSONID], [COMMENT_DATE], [COMMENTID], [ISPUBLIC])
                                        VALUES (?, ?, ?, ?, ?)
                                    """, data)

                                    connection.commit()
                                except pyodbc.IntegrityError:
                                    print(f"Skipping ticket {all_ids[pageCount]}")
                                    pageCount += 1
                                    url = f"https://capellasupport.zendesk.com/api/v2/tickets/{all_ids[pageCount]}/comments"

                        except TypeError:
                            pageCount += 1
                            url = f"https://capellasupport.zendesk.com/api/v2/tickets/{all_ids[pageCount]}/comments"
                            print(f"Gathering information from ticket: {all_ids[pageCount]}")

        except AttributeError:
            print(f"Skipping ticket {all_ids[pageCount]}")
            pageCount += 1
            url = f"https://capellasupport.zendesk.com/api/v2/tickets/{all_ids[pageCount]}/comments"
            print(f"Gathering information from ticket: {all_ids[pageCount]}")
