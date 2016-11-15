EasterTheHarvester
================

This java command line program uses a list of cities and keywords 
to search the web for pertinent emails. Currently it uses Google
Maps to find websites before scraping those sites for emails.

Use at your own risk and keep it classy.

Usage:

java eastertheharvester

required input values:

-d [databasename]
-u [databaseuser]
-p [databasepass]
-k [keywords with space between]

options:

-maxThreads [integer] (defaults to 20)
-host [databasehost] (defaults to localhost)
-port [databaseport] (defaults to 5432)
-desc [description] (flag to categorize emails; defaults to default)
-schema (flag to create fresh database schema)
-cities [filename] (txt file containing cities for program to search; saves to database after first use)

Setup:

Create a postgresql database and use the -schema flag to create the schema

Create a tabbed text file of cities to search. Populate the database with the -cities flag and supply the filename. File should have format:
country	zipcode	city state stateabbreviation unused unused unused unused latitude longitude

You’ll have to find or make this.