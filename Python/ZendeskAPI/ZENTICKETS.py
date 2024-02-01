import requests
import pyodbc
from datetime import datetime, timedelta

def get_epoch_three_months_ago():
    current_datetime = datetime.now()
    three_months_ago = current_datetime - timedelta(days=3 * 30)
    epoch_timestamp = int(three_months_ago.timestamp())

    return epoch_timestamp

url = f"https://capellasupport.zendesk.com/api/v2/incremental/tickets/cursor?start_time={get_epoch_three_months_ago()}"

headers = {
    "Content-Type": "application/json",
}

# Replace 'YOUR_EMAIL' and 'YOUR_API_TOKEN' with your actual email and API token
auth = ("aidan.horne@capellaconsulting.co.nz/token", "XHUm0W0pXTa2Ma7kkWdAWw5hYiHElbg6Pu1nUCAO")

# Construct connection string
connection_string = connection_string = 'DRIVER={SQL Server};SERVER=CapDBServer;DATABASE=capella;UID=aidanh;PWD=Quack1nce4^'

# Establish connection
connection = pyodbc.connect(connection_string)
cursor = connection.cursor()

def isInt(var):
    try:
        int(var)
        return var
    except TypeError:
        return None

def sortTags(tags):
    try:    
        result = ""
        for tag in tags:
            result += tag + ", "
        
        return result
    except TypeError:
        return None

while url is not None:
    with requests.Session() as session:
        session.headers.update(headers)
        session.auth = auth
        response = session.get(url)
        
        try:
            if response.status_code == 200:
                data = response.json()
                for result in data.values():
                    if result is not None:
                            for var in result:
                                ticketID = var.get("id"),
                                status = var.get("status"),
                                subject = var.get("subject"),
                                ticketType = var.get("type"),
                                dateCreated = var.get("created_at"),
                                dateUpdated = var.get("updated_at"),
                                requestedId = var.get("requester_id"),
                                assigneeId = var.get("assignee_id"),
                                submitted_id = var.get("submitter_id"),
                                orgId = var.get("organization_id"),
                                groupId = var.get("group_id"),
                                if len(var.get("tags")) > 1:
                                    tags = ', '.join(str(item) for item in var.get("tags"))
                                elif len(var.get("tags")) == 0:
                                    tags = ""
                                else:
                                    tags = var.get("tags")
                                    tags = tags[0]
                                dueDate = var.get("due_at"),
                                satRating = var.get("satisfaction_rating"),
                                satisfaction_rating_dict, = satRating
                                satRating = ', '.join(map(str, satisfaction_rating_dict.values()))
                                satRating = satRating[:75] + (satRating[75:] and '..')
                                priority = var.get("priority"),
                                problemId = var.get("problem_id")
                                
                                if dateCreated[0] is not None:
                                    dateCObject = datetime.strptime(dateCreated[0], "%Y-%m-%dT%H:%M:%SZ")
                                else:
                                    dateCObject = None
                                if dateUpdated[0] is not None:
                                    dateUObject = datetime.strptime(dateUpdated[0], "%Y-%m-%dT%H:%M:%SZ")
                                else:
                                    dateUObject = None
                                if dueDate[0] is not None:
                                    dateDObject = datetime.strptime(dueDate[0], "%Y-%m-%dT%H:%M:%SZ")
                                else:
                                    dateDObject = None

                                data = [isInt(ticketID[0]), status[0], subject[0],ticketType[0], dateCObject, dateUObject,isInt(requestedId[0]), isInt(assigneeId[0]),
                                        isInt(submitted_id[0]),isInt(orgId[0]), isInt(groupId[0]), tags,dateDObject, sortTags(satRating), sortTags(priority), isInt(problemId)]
                                try:
                                    cursor.execute("""
                                        INSERT INTO ZEN_TICKETS ([TICKETID], [STATUS], [SUBJECT], [TICKETTYPE], [DATECREATED], [DATEUPDATED], [REQUESTED_ID], [ASSIGNEE_ID],
                                        [SUBMITTER_ID], [ORG_ID], [GROUP_ID], [TAGS], [DUEDATE], [SAT_RATING], [PRIORITY], [PROBLEM_ID])
                                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                                    """, data)
                                    print(f"ticket #{ticketID[0]}")
                                    connection.commit()
                                except pyodbc.IntegrityError:
                                    print(f"skipping ticket #{ticketID[0]}")
                                    pass
                    else:
                        break

        except AttributeError:
            print("End of ticket stream")
            url = None

cursor.close()
connection.close()