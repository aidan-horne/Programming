import requests
import pyodbc
from datetime import datetime

url = "https://capellasupport.zendesk.com/api/v2/groups.json"
url = "https://capellasupport.zendesk.com/api/v2/ticket_metrics?page=1"

headers = {
    "Content-Type": "application/json",
}

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

def dictToString(var):
    result_string = ""
    for key, value in var.items():
        result_string += f"{key}: {value}, "
    
    return result_string

# Create a session for more verbose output
while url is not None:
    with requests.Session() as session:
        session.headers.update(headers)
        session.auth = auth
        print(url)
        response = session.get(url)
        try:
            if response.status_code == 200:
                # Successful request
                data = response.json()
                for result in data.values():
                    if result is not None:
                            for var in result:

                                ticketID = var.get("ticket_id"),
                                metricCreated = var.get("created_at"),
                                metricUpdated = var.get("updated_at"),
                                replies = var.get("replies"),
                                assigneeUpdatedAt = var.get("assignee_updated_at"),
                                requesterUpdatedAt = var.get("requester_updated_at"),
                                statusUpdatedAt = var.get("status_updated_at"),
                                initallyAssignedAt = var.get("initially_assigned_at"),
                                assignedAt = var.get("assigned_at"),
                                solvedAt = var.get("solved_at"),
                                latestCommentAddedAt = var.get("latest_comment_added_at"),
                                replyTimeInMinutes = var.get("reply_time_in_minutes"),
                                firstResoutionInTimeInMins = var.get("first_resolution_time_in_minutes"),
                                fullyResolutionTimeInMins = var.get("full_resolution_time_in_minutes"),
                                agentWaitTimeInMins = var.get("agent_wait_time_in_minutes"),
                                requesterWaitTimeInMins = var.get("requester_wait_time_in_minutes")
                                onHoldTimeInMinutes = var.get("on_hold_time_in_minutes")         

                                replyTimeInMinutes = list(replyTimeInMinutes[0].items())[0][1]
                                firstResoutionInTimeInMins = list(firstResoutionInTimeInMins[0].items())[0][1]
                                fullyResolutionTimeInMins = list(fullyResolutionTimeInMins[0].items())[0][1]
                                agentWaitTimeInMins = list(agentWaitTimeInMins[0].items())[0][1]
                                requesterWaitTimeInMins = list(requesterWaitTimeInMins.items())[0][1]
                                onHoldTimeInMinutes = list(onHoldTimeInMinutes.items())[0][1]
                                
                                if metricCreated[0] is not None:
                                    metricCreatedObject = datetime.strptime(metricCreated[0], "%Y-%m-%dT%H:%M:%SZ")
                                else:
                                    metricCreated = None
                                if metricUpdated[0] is not None:
                                    metricUpdatedObject = datetime.strptime(metricUpdated[0], "%Y-%m-%dT%H:%M:%SZ")
                                else:
                                    metricUpdated = None
                                if assigneeUpdatedAt[0] is not None:
                                    assigneeUpdatedAtObject = datetime.strptime(assigneeUpdatedAt[0], "%Y-%m-%dT%H:%M:%SZ")
                                else:
                                    assigneeUpdatedAtObject = None
                                if requesterUpdatedAt[0] is not None:
                                    requesterUpdatedAtObject = datetime.strptime(requesterUpdatedAt[0], "%Y-%m-%dT%H:%M:%SZ")
                                else:
                                    requesterUpdatedAt = None
                                if statusUpdatedAt[0] is not None:
                                    statusUpdatedAtObject = datetime.strptime(statusUpdatedAt[0], "%Y-%m-%dT%H:%M:%SZ")
                                else:
                                    statusUpdatedAtObject = None
                                if initallyAssignedAt[0] is not None:
                                    initallyAssignedAtObject = datetime.strptime(initallyAssignedAt[0], "%Y-%m-%dT%H:%M:%SZ")
                                else:
                                    initallyAssignedAtObject = None
                                if assignedAt[0] is not None:
                                    assignedAtObject = datetime.strptime(assignedAt[0], "%Y-%m-%dT%H:%M:%SZ")
                                else:
                                    assignedAtObject = None
                                if solvedAt[0] is not None:
                                    solvedAtObject = datetime.strptime(solvedAt[0], "%Y-%m-%dT%H:%M:%SZ")
                                else:
                                    solvedAtObject = None
                                if latestCommentAddedAt[0] is not None:
                                    latestCommentAddedAtObject = datetime.strptime(latestCommentAddedAt[0], "%Y-%m-%dT%H:%M:%SZ")
                                else:
                                    latestCommentAddedAtObject = None

                                data = [isInt(ticketID[0]), metricCreatedObject, metricUpdatedObject,isInt(replies[0]), assigneeUpdatedAtObject ,requesterUpdatedAtObject,
                                        statusUpdatedAtObject,initallyAssignedAtObject, assignedAtObject, solvedAtObject,latestCommentAddedAtObject, isInt(replyTimeInMinutes), 
                                        isInt(firstResoutionInTimeInMins), isInt(fullyResolutionTimeInMins), isInt(agentWaitTimeInMins), isInt(requesterWaitTimeInMins), isInt(onHoldTimeInMinutes)]
                                        
                                try:
                                    cursor.execute("""
                                        INSERT INTO ZEN_METRICS ([TICKET_ID], [METRIC_CREATED], [METRIC_UPDATED], [REPLIES], [ASSIGNEE_UPDATED_AT], [REQUESTER_UPDATED_AT],
                                                    [STATUS_UPDATED_AT], [INITIALLY_ASSIGNED_AT], [ASSIGNED_AT], [SOLVED_AT], [LASTEST_COMMENT_ADDED_AT], [REPLY_TIME_IN_MINS], 
                                                    [FIRST_RESOLUTION_TIME_IN_MINS], [FULL_RESOLUTION_TIME_IN_MINS], [AGENT_WAIT_TIME_IN_MINS], [REQUESTER_WAIT_IN_MINS], [ON_HOLD_TIME_IN_MINS])
                                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                                    """, data)

                                    connection.commit()
                                except pyodbc.IntegrityError:
                                    # print(pyodbc.IntegrityError)
                                    # cursor.close()
                                    # connection.close()
                                    # exit()
                                    pass
        except AttributeError:
            x = response.json()
            url = list(x.values())[1]
