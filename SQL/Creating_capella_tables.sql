
/* DROP TABLE ZENDESK_SNAPSHOT
DROP TABLE ZEN_COMMENTS
DROP TABLE ZEN_GROUPS
DROP TABLE ZEN_METRICS 
DROP TABLE ZEN_ORGS
DROP TABLE ZEN_METRICS_STAGING
DROP TABLE ZEN_PEOPLE
DROP TABLE ZEN_TICKETS
DROP TABLE ZEN_TICKETS_MIG
DROP TABLE ZEN_TICKETS_ORG
DROP TABLE ZEN_TICKETS_SOLVED_BY
DROP TABLE ZEN_TICKETS_STAGING
DROP TABLE ZEN_TRANSFERS
*/

CREATE TABLE ZENDESK_SNAPSHOT (
		DATE DATE, 
		STATUS VARCHAR(15), 
		TICKET_COUNT INTEGER, 
		AGE VARCHAR(20), 
		ORG_ID BIGINT, 
		GROUP_ID BIGINT, 
		ASSIGNED_ID BIGINT
	)



CREATE TABLE ZEN_COMMENTS (
		TICKETID INTEGER, 
		PERSONID BIGINT, 
		COMMENT_DATE Date, 
		COMMENTID BIGINT, 
		ISPUBLIC SMALLINT
	)



CREATE TABLE ZEN_GROUPS (
		GROUPID BIGINT, 
		[GROUP] VARCHAR(30)
	)



CREATE TABLE ZEN_METRICS (
		TICKET_ID INTEGER NOT NULL, 
		METRIC_CREATED Date, 
		METRIC_UPDATED Date, 
		REPLIES INTEGER, 
		ASSIGNEE_UPDATED_AT Date, 
		REQUESTER_UPDATED_AT Date, 
		STATUS_UPDATED_AT Date, 
		INITIALLY_ASSIGNED_AT Date, 
		ASSIGNED_AT Date, 
		SOLVED_AT Date, 
		LASTEST_COMMENT_ADDED_AT Date, 
		REPLY_TIME_IN_MINS INTEGER, 
		FIRST_RESOLUTION_TIME_IN_MINS INTEGER, 
		FULL_RESOLUTION_TIME_IN_MINS INTEGER, 
		AGENT_WAIT_TIME_IN_MINS INTEGER, 
		REQUESTER_WAIT_IN_MINS INTEGER, 
		ON_HOLD_TIME_IN_MINS INTEGER
	)



CREATE TABLE ZEN_METRICS_STAGING (
		TICKET_ID INTEGER NOT NULL, 
		METRIC_CREATED Date, 
		METRIC_UPDATED Date, 
		REPLIES INTEGER, 
		ASSIGNEE_UPDATED_AT Date, 
		REQUESTER_UPDATED_AT Date, 
		STATUS_UPDATED_AT Date, 
		INITIALLY_ASSIGNED_AT Date, 
		ASSIGNED_AT Date, 
		SOLVED_AT Date, 
		LASTEST_COMMENT_ADDED_AT Date, 
		REPLY_TIME_IN_MINS INTEGER, 
		FIRST_RESOLUTION_TIME_IN_MINS INTEGER, 
		FULL_RESOLUTION_TIME_IN_MINS INTEGER, 
		AGENT_WAIT_TIME_IN_MINS INTEGER, 
		REQUESTER_WAIT_IN_MINS INTEGER, 
		ON_HOLD_TIME_IN_MINS INTEGER
	)



CREATE TABLE ZEN_ORGS (
		ORG_ID BIGINT, 
		ORGNAME VARCHAR(50), 
		DOMAIN_NAMES VARCHAR(200), 
		DETAILS VARCHAR(200)
	)



CREATE TABLE ZEN_PEOPLE (
		PID BIGINT, 
		NAME VARCHAR(100), 
		EMAIL VARCHAR(100), 
		CREATEDATE Date, 
		PHONE VARCHAR(30), 
		ROLE VARCHAR(20), 
		VERIFIED VARCHAR(20)
	)



CREATE TABLE ZEN_TICKETS (
		TICKETID INTEGER NOT NULL, 
		STATUS VARCHAR(20), 
		SUBJECT VARCHAR(300), 
		TICKETTYPE VARCHAR(20), 
		DATECREATED Date, 
		DATEUPDATED Date, 
		REQUESTED_ID BIGINT, 
		ASSIGNEE_ID BIGINT, 
		SUBMITTER_ID BIGINT, 
		ORG_ID BIGINT, 
		GROUP_ID BIGINT, 
		TAGS VARCHAR(250), 
		DUEDATE Date, 
		SAT_RATING VARCHAR(10), 
		PRIORITY VARCHAR(10), 
		PROBLEM_ID INTEGER
	)



CREATE TABLE ZEN_TICKETS_MIG (
		TICKETID INTEGER NOT NULL, 
		STATUS VARCHAR(20), 
		SUBJECT VARCHAR(300), 
		TICKETTYPE VARCHAR(20), 
		DATECREATED Date, 
		DATEUPDATED Date, 
		REQUESTED_ID BIGINT, 
		ASSIGNEE_ID BIGINT, 
		SUBMITTER_ID BIGINT, 
		ORG_ID BIGINT, 
		GROUP_ID BIGINT, 
		TAGS VARCHAR(250), 
		DUEDATE Date, 
		SAT_RATING VARCHAR(10), 
		PRIORITY VARCHAR(10), 
		PROBLEM_ID INTEGER
	)



CREATE TABLE ZEN_TICKETS_ORG (
		TICKETID INTEGER NOT NULL, 
		STATUS VARCHAR(20), 
		SUBJECT VARCHAR(300), 
		TICKETTYPE VARCHAR(20), 
		DATECREATED Date, 
		DATEUPDATED Date, 
		REQUESTED_ID BIGINT, 
		ASSIGNEE_ID BIGINT, 
		SUBMITTER_ID BIGINT, 
		ORG_ID BIGINT, 
		GROUP_ID BIGINT, 
		TAGS VARCHAR(250), 
		DUEDATE Date, 
		SAT_RATING VARCHAR(10), 
		PRIORITY VARCHAR(10), 
		PROBLEM_ID INTEGER
	)



CREATE TABLE ZEN_TICKETS_SOLVED_BY (
		TICKETID INTEGER, 
		EVENTID BIGINT, 
		PID BIGINT, 
		STATUS VARCHAR(10), 
		CREATED_AT Date
	)



CREATE TABLE ZEN_TICKETS_STAGING (
		TICKETID INTEGER NOT NULL, 
		STATUS VARCHAR(20), 
		SUBJECT VARCHAR(300), 
		TICKETTYPE VARCHAR(20), 
		DATECREATED Date, 
		DATEUPDATED Date, 
		REQUESTED_ID BIGINT, 
		ASSIGNEE_ID BIGINT, 
		SUBMITTER_ID BIGINT, 
		ORG_ID BIGINT, 
		GROUP_ID BIGINT, 
		TAGS VARCHAR(250), 
		DUEDATE Date, 
		SAT_RATING VARCHAR(10), 
		PRIORITY VARCHAR(10), 
		PROBLEM_ID BIGINT
	)



CREATE TABLE ZEN_TRANSFERS (
		TICKETID INTEGER, 
		EVENTID BIGINT, 
		PREVIOUS_VALUE BIGINT, 
		VALUE BIGINT, 
		CREATED_AT Date
	)