# Howard University Event Registration System

AWS Cloud Architecture | Brian Too | November 18 2026

## 1 Cloud provider and rationale

I chose Amazon Web Services (AWS). Elastic Beanstalk can host a Java web application, while Amazon RDS can store related event, attendee, and registration records. This keeps routine infrastructure management with AWS and leaves the application responsible for its business rules. The proposed production design uses the Northern Virginia region (us-east-1) and two Availability Zones. It is a design proposal; no AWS resources have been deployed.

## 2 Java execution environment

The Java application would run on Amazon EC2 instances managed by a load-balanced AWS Elastic Beanstalk environment, with at least two instances across the two zones. A future web/API adapter would expose the existing library through HTTPS endpoints. Beanstalk would manage the environment and instance scaling; the team would still maintain Java dependencies, application releases, and configuration. This is appropriate for an introductory Java project because it avoids managing a container orchestration platform. [1, 2]

## 3 Application data storage

Amazon RDS for PostgreSQL would store Events, Attendees, and Registrations. Primary keys would enforce unique record IDs, foreign keys would enforce event and attendee relationships, and a unique (attendee_id, event_id) constraint would prevent duplicate participation, including canceled records. SQL transactions would lock an event row before checking its status and counting active registrations, then insert the new registration before committing. Event edits, closure, cancellation, and seat release would follow the same locking policy so concurrent application instances cannot oversell seats. RDS supplies managed database operations and backup capabilities. [3]

## 4 User access and request flow

A future browser client would sign in through Amazon Cognito and send an access token with its HTTPS request to an Application Load Balancer. The load balancer would route the request to a healthy Beanstalk instance. The Java API would validate the token and permissions, execute the registration transaction in RDS, and return a confirmation. For event creation, the API would require an organizer role derived from verified identity claims. A university identity provider could be federated through SAML if Howard approves and configures that integration; existing campus SSO integration is not claimed. [4, 5]

**Implementation boundary:** The submitted Java library uses in-memory maps and JUnit tests. An HTTP adapter, identity integration, JDBC persistence, and AWS infrastructure are future work required to realize this design.

## 5 Application and data security

Authentication and authorization. Cognito would handle sign-in. The Java adapter would verify token signature, issuer, expiration, token use, and intended app client. Organizer permissions would protect event creation and management. Attendee endpoints would derive the attendee identity from the authenticated account and check ownership before viewing or canceling registrations. The library's UserRole argument is a trusted input for local testing, not proof that a network caller is authenticated. [5]

Network and encryption. The load balancer would accept HTTPS using an AWS Certificate Manager certificate and redirect HTTP to HTTPS. Application instances and RDS would reside in private subnets without public IP addresses. Security groups would allow the load balancer to reach only the application port, and allow PostgreSQL access only from the application security group. Private instances would use controlled outbound access through NAT for required external services. TLS would also protect load-balancer-to-application traffic and JDBC connections, with database certificate and hostname validation. RDS storage and backups would be encrypted with AWS KMS keys. [3, 6]

Credentials and access. Database credentials would be stored in AWS Secrets Manager, retrieved through the EC2 instance role, and rotated. The runtime would use a restricted database account, not the database administrator account. IAM would grant only the specific secret, logging, and encryption permissions needed. Secrets would never be committed to GitHub or included in logs. Administrative AWS access would require MFA, and attendee names, email addresses, and tokens would be omitted from routine operational logs. [7]

## 6 Health monitoring and logs

Elastic Beanstalk enhanced health reporting and load balancer health checks would detect unhealthy instances. Application and environment logs would stream to CloudWatch Logs with an explicit retention period. CloudWatch alarms would notify maintainers through Amazon SNS when error rates, response latency, CPU usage, database connections, or free database storage cross chosen thresholds. Structured logs would include request IDs and failure reasons without sensitive data. A readiness endpoint would check application readiness and database connectivity; an AWS CloudTrail audit trail would record infrastructure changes. [8]

## 7 Increased demand

Elastic Beanstalk Auto Scaling would add EC2 instances as request volume or CPU rises, within a configured maximum and budget. Stateless API instances would share RDS rather than keep separate in-memory registration maps. Bounded database connection pools and indexes on event_id, attendee_id, and status would limit database load. If RDS becomes the bottleneck, the team could increase its instance size and tune queries. Registration capacity decisions would always use transactions on the primary database, not a potentially stale read replica. [2]

## 8 Application server failure

If an instance fails, load balancer health checks would stop sending it requests and the other zone's healthy instance would continue serving traffic. Auto Scaling would replace the failed instance. Requests already in progress may fail; clients would retry safely and retrieve the existing registration when the unique attendee-event pair already exists. A committed registration would survive because data lives in RDS. A Multi-AZ RDS DB instance would maintain a synchronous standby in the other zone and support database failover. Failover can interrupt connections; the application must reconnect and retry failed transactions appropriately. The standby is for availability and cannot serve read traffic. Backups would support recovery from accidental changes; Multi-AZ alone is not a backup. [2, 9]

## 9 Monthly operating cost

The main costs would be EC2 instance hours and attached storage, load balancer hours and processed traffic, RDS instance hours and storage, the Multi-AZ standby, backups beyond included allowances, NAT gateway hours and data processing, public IPv4 addresses, and network data transfer. CloudWatch log ingestion and retention, Cognito usage, Secrets Manager, KMS requests, and optional DNS hosting would add costs. Beanstalk has no separate service charge; its underlying resources are billed. The two-instance, Multi-AZ design trades a higher baseline cost for availability. A disposable single-instance development environment could reduce cost, with lower availability. AWS Budgets alerts, scaling caps, and short log retention would help control spending. A dollar estimate would require selected instance sizes, traffic, storage, and current regional prices; free usage is not assumed. [10]

## References

1. [Java and RDS with Elastic Beanstalk](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/java-rds.html)
2. [Elastic Beanstalk environment types](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/using-features-managing-env-types.html)
3. [RDS concepts and security capabilities](https://docs.aws.amazon.com/AmazonRDS/latest/gettingstartedguide/concepts.html)
4. [Cognito SAML identity providers](https://docs.aws.amazon.com/cognito/latest/developerguide/cognito-user-pools-managing-saml-idp.html)
5. [Cognito token verification](https://docs.aws.amazon.com/cognito/latest/developerguide/amazon-cognito-user-pools-using-tokens-verifying-a-jwt.html)
6. [RDS TLS connections](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/UsingWithRDS.SSL.html)
7. [RDS and Secrets Manager](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/rds-secrets-manager.html)
8. [Elastic Beanstalk and CloudWatch Logs](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/AWSHowTo.cloudwatchlogs.html)
9. [RDS Multi-AZ DB instances](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/Concepts.MultiAZSingleStandby.html)
10. [Elastic Beanstalk pricing](https://aws.amazon.com/elasticbeanstalk/pricing/)
