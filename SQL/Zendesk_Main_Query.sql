SELECT CASE WHEN ZEN_ORGS.ORGNAME is NULL then 'Unknown' ELSE ZEN_ORGS.ORGNAME END as COLUMN0
, ZEN_PEOPLE.NAME as COLUMN1

, ZEN_TICKETS.TICKETID as COLUMN2

, case when ZEN_TICKETS.STATUS = 'closed'

or ZEN_TICKETS.STATUS = 'solved' then 'closed' else ZEN_TICKETS.STATUS end as COLUMN3

, SUM(CASE WHEN ZEN_TICKETS.STATUS not in ('closed','solved') then 1 else 0 end) as COLUMN4

, ZEN_TICKETS.SUBJECT as COLUMN5

, CAST(ZEN_TICKETS.DATECREATED AS DATE) as COLUMN6

, CAST(ZEN_TICKETS.DATEUPDATED AS DATE) as COLUMN7

, ZEN_TICKETS.DATECREATED as COLUMN8

, ZEN_TICKETS.DATEUPDATED as COLUMN9

, ZEN_TICKETS.TAGS as COLUMN10

, case when tags like '%maas360%configuration%' then 'Configuration' when tags like '%maas360%licensing%' then 'Licensing' when tags like '%maas360%enrollment%' then 'Enrollment Issue' when tags like '%maas360%policy_update%' then 'Policy Update' when tags like '%maas360%app_dist%' then 'Application Dist' when tags like '%maas360%system_iss%' then 'System Issue' when tags like '%maas360%implementation%' then 'Implementation' when tags like '%maas360%customer_query%' then 'Customer Query' when tags like '%maas360%testing%' then 'Testing' when tags like '%maas360%documentation%' then 'Documentation' when tags like '%maas360%training%' then 'Training' else 'Other'end as COLUMN11

, ZEN_PEOPLE_2.NAME as COLUMN12

, ZEN_GROUPS.[GROUP] as COLUMN13

, ZEN_PEOPLE_3.NAME as COLUMN14

, Count(distinct ZEN_TICKETS.TICKETID) as COLUMN15

, case when DATEDIFF(day, CURRENT_TIMESTAMP,ZEN_TICKETS.DATECREATED) <=5 then '1-5 Days' when DATEDIFF(day, CURRENT_TIMESTAMP,ZEN_TICKETS.DATECREATED) <=10 then '6-10 Days' when DATEDIFF(day, CURRENT_TIMESTAMP,ZEN_TICKETS.DATECREATED) <=30 then '10-30 Days' when DATEDIFF(day, CURRENT_TIMESTAMP,ZEN_TICKETS.DATECREATED) <=90 then '31-90 Days' when DATEDIFF(day, CURRENT_TIMESTAMP,ZEN_TICKETS.DATECREATED) <=180 then '3-6 Months' else '6 months

and over' end as COLUMN16

, DATEDIFF(day, CURRENT_TIMESTAMP,ZEN_TICKETS.DATECREATED) as COLUMN17

, case when DATEDIFF(day, CURRENT_TIMESTAMP,ZEN_TICKETS.DATECREATED) <=5 then 1 when DATEDIFF(day, CURRENT_TIMESTAMP,ZEN_TICKETS.DATECREATED) <=10 then 2 when DATEDIFF(day, CURRENT_TIMESTAMP,ZEN_TICKETS.DATECREATED) <=30 then 3 when DATEDIFF(day, CURRENT_TIMESTAMP,ZEN_TICKETS.DATECREATED) <=90 then 4 when DATEDIFF(day, CURRENT_TIMESTAMP,ZEN_TICKETS.DATECREATED) <=180 then 5 else 6 end as COLUMN18

, ZEN_ORGS.ORG_ID as COLUMN19

, case when ZEN_TICKETS.PRIORITY is NULL then 'Not Set' else ZEN_TICKETS.PRIORITY end as COLUMN20

, ZEN_TICKETS.TICKETTYPE as COLUMN21

, CAST(ZEN_TICKETS.DUEDATE AS DATE) as COLUMN22

, CONCAT(trim(CHAR(YEAR(ZEN_TICKETS.DATECREATED))),' Wk ', trim(CHAR(datepart(wk, ZEN_TICKETS.DATECREATED)))) as COLUMN23

, YEAR(ZEN_TICKETS.DATECREATED) as COLUMN24

, datepart(wk, ZEN_TICKETS.DATECREATED) as COLUMN25

, ZEN_TICKETS.ASSIGNEE_ID as COLUMN26

, ZEN_TICKETS.REQUESTED_ID as COLUMN27

, ZEN_TICKETS.SAT_RATING as COLUMN28

, case when sum(case when ZEN_TICKETS.SAT_RATING != 'unoffered' then 1 else 0 end) = 0 then 0 else (sum(case when ZEN_TICKETS.SAT_RATING != 'unoffered' then 1 else 0 end)-sum(case when ZEN_TICKETS.SAT_RATING = 'bad' then 1 else 0 end))/sum(case when ZEN_TICKETS.SAT_RATING != 'unoffered' then 1 else 0 end)*100 end as COLUMN29

, sum(case when ZEN_TICKETS.STATUS in ('closed','solved') then 1 else 0 end) as COLUMN30

, sum(case when ZEN_TICKETS.STATUS not in ('closed','solved') then 1 else 0 end) as COLUMN31

, sum(case when ZEN_TICKETS.STATUS = 'open' then 1 else 0 end) as COLUMN32

, sum(case when ZEN_TICKETS.STATUS = 'pending' then 1 else 0 end) as COLUMN33

, sum(case when ZEN_TICKETS.SAT_RATING != 'unoffered' then 1 else 0 end) as COLUMN34

, sum(case when ZEN_TICKETS.SAT_RATING in ('good','bad') then 1 else 0 end) as COLUMN35

, ZEN_METRICS.METRIC_CREATED as COLUMN36

, ZEN_METRICS.METRIC_UPDATED as COLUMN37

, ZEN_METRICS.REPLIES as COLUMN38

, ZEN_METRICS.ASSIGNEE_UPDATED_AT as COLUMN39

, ZEN_METRICS.REQUESTER_UPDATED_AT as COLUMN40

, ZEN_METRICS.STATUS_UPDATED_AT as COLUMN41

, ZEN_METRICS.INITIALLY_ASSIGNED_AT as COLUMN42

, ZEN_METRICS.ASSIGNED_AT as COLUMN43

, ZEN_METRICS.SOLVED_AT as COLUMN44

, ZEN_METRICS.LASTEST_COMMENT_ADDED_AT as COLUMN45

, sum(ZEN_METRICS.REPLY_TIME_IN_MINS) as COLUMN46

, sum(ZEN_METRICS.FIRST_RESOLUTION_TIME_IN_MINS) as COLUMN47

, sum(ZEN_METRICS.FULL_RESOLUTION_TIME_IN_MINS) as COLUMN48

, sum(ZEN_METRICS.AGENT_WAIT_TIME_IN_MINS) as COLUMN49

, sum(ZEN_METRICS.REQUESTER_WAIT_IN_MINS) as COLUMN50

, sum(ZEN_METRICS.ON_HOLD_TIME_IN_MINS) as COLUMN51

, avg(ZEN_METRICS.REPLY_TIME_IN_MINS) as COLUMN52

, avg(ZEN_METRICS.FIRST_RESOLUTION_TIME_IN_MINS) as COLUMN53

, avg(ZEN_METRICS.AGENT_WAIT_TIME_IN_MINS) as COLUMN54

, avg(ZEN_METRICS.REQUESTER_WAIT_IN_MINS) as COLUMN55

, avg(ZEN_METRICS.ON_HOLD_TIME_IN_MINS) as COLUMN56

, avg(DATEDIFF(minute, CURRENT_TIMESTAMP, ZEN_METRICS.ASSIGNED_AT) * 0.016667) as COLUMN57

, SUM(CASE WHEN TICKETTYPE ='problem'

and DATEDIFF(hour, CURRENT_TIMESTAMP, DATEADD(hour, 12, DATECREATED)) > 72 THEN 1 ELSE 0 END) as COLUMN58

, SUM(CASE WHEN PRIORITY='low'

and DATEDIFF(hour, DATEADD(hour, 12, CREATED_AT), DATECREATED)>240 then 1 ELSE 0 END) as COLUMN59

, ZEN_TICKETS.PROBLEM_ID as COLUMN60

, TRIM(CASE WHEN ZEN_TICKETS.TAGS NOT LIKE '%change^_^_%' ESCAPE '^' THEN 'ERROR' WHEN ZEN_TICKETS.TAGS LIKE '%approved,%' THEN SUBSTRING(ZEN_TICKETS.TAGS, CHARINDEX('__', ZEN_TICKETS.TAGS, 1) + 2, LEN(ZEN_TICKETS.TAGS)- CHARINDEX('__', ZEN_TICKETS.TAGS, 1)+2)WHEN ZEN_TICKETS.TAGS LIKE '%,%' 
	THEN SUBSTRING(ZEN_TICKETS.TAGS, CHARINDEX('__', ZEN_TICKETS.TAGS, 1) + 2, CHARINDEX(',', ZEN_TICKETS.TAGS, 1) - CHARINDEX('__', ZEN_TICKETS.TAGS, 1) - 2) ELSE SUBSTRING(ZEN_TICKETS.TAGS, 
	CHARINDEX('__', ZEN_TICKETS.TAGS, 1) + 2, CHARINDEX('server', ZEN_TICKETS.TAGS, 1) + 6) END) as COLUMN61

, CASE WHEN ZEN_TICKETS.TAGS like '%change^_^_%' ESCAPE '^' then 'CHANGE' else NULL END as COLUMN62

, CASE WHEN TAGS like '%change^_^_%' ESCAPE '^' THEN (CASE WHEN TAGS like '%successful%' then 'Successful' WHEN TAGS like '%approved%' then 'Approved' WHEN TAGS like '%request%' then 'Request' WHEN TAGS like '%failed%' then 'Failed' else 'N/A' END ) ELSE 'N/A' END as COLUMN63

, SUBSTRING(CONVERT(VARCHAR, ZEN_TICKETS.DATECREATED, 120), 1, 7) as COLUMN64

, 'Security Bulletin Reviews' as COLUMN65

, CAP_TICKET_MONTHLY_REPORTING.CAP_TICKET_MONTHLY_DESC as COLUMN66

, CAP_TICKET_MONTHLY_REPORTING.CAP_TICKET_MONTHLY_UPDATE as COLUMN67

, 32880 as COLUMN68

, CAP_TICKET_MONTHLY_REPORTING.GUID as COLUMN69

, cast((sum((case when ZEN_METRICS.REPLY_TIME_IN_MINS > 60 then 0.0000 else 1.0000 end))*1.0000)/(count(*)*1.0000)*100 as decimal(5,2)) as COLUMN70

, cast((sum(case when (ZEN_TICKETS.PRIORITY = 'high'

or ZEN_TICKETS.PRIORITY = 'urgent') then case when ZEN_METRICS.FIRST_RESOLUTION_TIME_IN_MINS > 180 then 0.0000 else 1.0000 end when ZEN_TICKETS.PRIORITY = 'medium' then case when ZEN_METRICS.FIRST_RESOLUTION_TIME_IN_MINS > 480 then 0.0000 else 1.0000 end else case when ZEN_METRICS.FIRST_RESOLUTION_TIME_IN_MINS > 1440 then 0.0000 else 1.0000 end end))/count(*)*100.0000 as decimal(5,2)) as COLUMN71

FROM dbo.ZEN_TICKETS as ZEN_TICKETS

FULL JOIN dbo.ZEN_PEOPLE as ZEN_PEOPLE_2 on ZEN_TICKETS.ASSIGNEE_ID = ZEN_PEOPLE_2.PID

LEFT OUTER JOIN dbo.ZEN_GROUPS as ZEN_GROUPS on ZEN_TICKETS.GROUP_ID = ZEN_GROUPS.GROUPID

LEFT OUTER JOIN dbo.ZEN_ORGS as ZEN_ORGS on ZEN_TICKETS.ORG_ID = ZEN_ORGS.ORG_ID

FULL JOIN dbo.ZEN_PEOPLE as ZEN_PEOPLE on ZEN_TICKETS.REQUESTED_ID = ZEN_PEOPLE.PID

FULL JOIN dbo.ZEN_PEOPLE as ZEN_PEOPLE_3 on ZEN_TICKETS.SUBMITTER_ID = ZEN_PEOPLE_3.PID

LEFT OUTER JOIN dbo.ZEN_METRICS as ZEN_METRICS on ZEN_METRICS.TICKET_ID = ZEN_TICKETS.TICKETID

LEFT OUTER JOIN dbo.ZEN_TICKETS_SOLVED_BY as ZEN_TICKETS_LATEST_SOLVED_BY_V on ZEN_TICKETS.TICKETID = ZEN_TICKETS_LATEST_SOLVED_BY_V.TICKETID

LEFT OUTER JOIN dbo.CAP_TICKET_MONTHLY_REPORTING as CAP_TICKET_MONTHLY_REPORTING on ZEN_TICKETS.TICKETID = CAP_TICKET_MONTHLY_REPORTING.CAP_ZEN_TICKET_ID

WHERE (ZEN_TICKETS.TAGS NOT LIKE '%closed_by_merge%')

GROUP BY CASE WHEN ZEN_ORGS.ORGNAME is NULL then 'Unknown' Else ZEN_ORGS.ORGNAME END

, ZEN_PEOPLE.NAME

, ZEN_TICKETS.TICKETID

, case when ZEN_TICKETS.STATUS = 'closed'

or ZEN_TICKETS.STATUS = 'solved' then 'closed' else ZEN_TICKETS.STATUS end

, ZEN_TICKETS.SUBJECT

, CAST(ZEN_TICKETS.DATECREATED AS DATE)

, CAST(ZEN_TICKETS.DATEUPDATED AS DATE)

, ZEN_TICKETS.DATECREATED

, ZEN_TICKETS.DATEUPDATED

, ZEN_TICKETS.TAGS

, case when tags like '%maas360%configuration%' then 'Configuration' when tags like '%maas360%licensing%' then 'Licensing' when tags like '%maas360%enrollment%' then 'Enrollment Issue' when tags like '%maas360%policy_update%' then 'Policy Update' when tags like '%maas360%app_dist%' then 'Application Dist' when tags like '%maas360%system_iss%' then 'System Issue' when tags like '%maas360%implementation%' then 'Implementation' when tags like '%maas360%customer_query%' then 'Customer Query' when tags like '%maas360%testing%' then 'Testing' when tags like '%maas360%documentation%' then 'Documentation' when tags like '%maas360%training%' then 'Training' else 'Other'end

, ZEN_PEOPLE_2.NAME

, ZEN_GROUPS.[GROUP]

, ZEN_PEOPLE_3.NAME

, case when DATEDIFF(day, CURRENT_TIMESTAMP, ZEN_TICKETS.DATECREATED) <=5 then '1-5 Days' when DATEDIFF(day, CURRENT_TIMESTAMP, ZEN_TICKETS.DATECREATED) <=10 then '6-10 Days' when DATEDIFF(day, CURRENT_TIMESTAMP, ZEN_TICKETS.DATECREATED) <=30 then '10-30 Days' when DATEDIFF(day, CURRENT_TIMESTAMP, ZEN_TICKETS.DATECREATED) <=90 then '31-90 Days' when DATEDIFF(day, CURRENT_TIMESTAMP, ZEN_TICKETS.DATECREATED) <=180 then '3-6 Months' else '6 months

and over' end

, DATEDIFF(day, CURRENT_TIMESTAMP, ZEN_TICKETS.DATECREATED)

, case when DATEDIFF(day, CURRENT_TIMESTAMP, ZEN_TICKETS.DATECREATED) <=5 then 1 when DATEDIFF(day, CURRENT_TIMESTAMP, ZEN_TICKETS.DATECREATED) <=10 then 2 when DATEDIFF(day, CURRENT_TIMESTAMP, ZEN_TICKETS.DATECREATED) <=30 then 3 when DATEDIFF(day, CURRENT_TIMESTAMP, ZEN_TICKETS.DATECREATED) <=90 then 4 when DATEDIFF(day, CURRENT_TIMESTAMP, ZEN_TICKETS.DATECREATED) <=180 then 5 else 6 end

, ZEN_ORGS.ORG_ID

, case when ZEN_TICKETS.PRIORITY is NULL then 'Not Set' else ZEN_TICKETS.PRIORITY end

, ZEN_TICKETS.TICKETTYPE

, CAST(ZEN_TICKETS.DUEDATE AS DATE)

, CONCAT(trim(CHAR(YEAR(ZEN_TICKETS.DATECREATED))), ' Wk ', trim(CHAR(datepart(wk, ZEN_TICKETS.DATECREATED))))

, YEAR(ZEN_TICKETS.DATECREATED)

, datepart(wk, ZEN_TICKETS.DATECREATED)

, ZEN_TICKETS.ASSIGNEE_ID

, ZEN_TICKETS.REQUESTED_ID

, ZEN_TICKETS.SAT_RATING

, ZEN_METRICS.METRIC_CREATED

, ZEN_METRICS.METRIC_UPDATED

, ZEN_METRICS.REPLIES

, ZEN_METRICS.ASSIGNEE_UPDATED_AT

, ZEN_METRICS.REQUESTER_UPDATED_AT

, ZEN_METRICS.STATUS_UPDATED_AT

, ZEN_METRICS.INITIALLY_ASSIGNED_AT

, ZEN_METRICS.ASSIGNED_AT

, ZEN_METRICS.SOLVED_AT

, ZEN_METRICS.LASTEST_COMMENT_ADDED_AT

, ZEN_TICKETS.PROBLEM_ID

, TRIM(CASE WHEN ZEN_TICKETS.TAGS NOT LIKE '%change^_^_%' ESCAPE '^' THEN 'ERROR' 
	WHEN ZEN_TICKETS.TAGS LIKE '%approved,%' 
	THEN SUBSTRING(ZEN_TICKETS.TAGS, CHARINDEX('__', ZEN_TICKETS.TAGS, 1) + 2, 
	LEN(ZEN_TICKETS.TAGS)- 
	CHARINDEX('__', ZEN_TICKETS.TAGS, 1)+2)
	WHEN ZEN_TICKETS.TAGS LIKE '%,%' THEN SUBSTRING(ZEN_TICKETS.TAGS, 
	CHARINDEX('__', ZEN_TICKETS.TAGS, 1) + 2, CHARINDEX(',', ZEN_TICKETS.TAGS, 1) - 
	CHARINDEX('__', ZEN_TICKETS.TAGS, 1) - 2) ELSE SUBSTRING(ZEN_TICKETS.TAGS, 
	CHARINDEX('__', ZEN_TICKETS.TAGS, 1) + 2, CHARINDEX('server', ZEN_TICKETS.TAGS, 1) + 6) END)

, CASE WHEN ZEN_TICKETS.TAGS like '%change^_^_%' ESCAPE '^' then 'CHANGE' else NULL END

, CASE WHEN TAGS like '%change^_^_%' ESCAPE '^' THEN (CASE WHEN TAGS like '%successful%' then 'Successful' WHEN TAGS like '%approved%' then 'Approved' WHEN TAGS like '%request%' then 'Request' WHEN TAGS like '%failed%' then 'Failed' else 'N/A' END ) ELSE 'N/A' END

, SUBSTRING(CONVERT(VARCHAR, ZEN_TICKETS.DATECREATED, 120), 1, 7)

, CAP_TICKET_MONTHLY_REPORTING.CAP_TICKET_MONTHLY_DESC

, CAP_TICKET_MONTHLY_REPORTING.CAP_TICKET_MONTHLY_UPDATE

, CAP_TICKET_MONTHLY_REPORTING.GUID