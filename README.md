

[![Build Status](https://app.travis-ci.com/andreapagliacci/magnolia-cms-content-app-elasticsearch-s3.svg?branch=main)](https://app.travis-ci.com/andreapagliacci/magnolia-cms-content-app-elasticsearch-s3)
# What's problem resolve?
The app permits you to connect (now, in the future I expand the source connectable) to :
-   an **elastic search instance** shows/edit/add documents in the table from the index you prefer. You can search items with the Elastic Search engine directly in the specific tab. You have not only in Magnolia out-the-box without Kibana a tool for searching, adding, editing, delete an item on a specific index, but you have a framework for developers to map index, and fields to one DTO and facilitate import/export, and conversion from elastic search data to another connectable source).
-   An **S3 bucket** shows all files present, download/upload directly, and so on the same facilities to import/export/conversion from the connectable source. This last feature it's only for developers, but another will be added for managing conversion data, import, and export.
-   **It's a generic connector for a source**, simplifying the process to connect another source. It's necessary to write 3 classes for enabling another source and have added/edit/showing items. (See this [article](https://www.andreapagliacci.it/how-to-implements-new-source-content-app-for-magnolia-cms/))


If you want to show a video how to appare the app go to ![Magnolia CMS content app: Elastic Search and S3](./docs/Cover-video-elasticsearch-app.png)](https://www.youtube.com/watch?v=lo9L0f0ctl8 "Magnolia CMS content app: Elastic Search and S3")

For more informations see [How to implements new source](https://www.andreapagliacci.it/how-to-implements-new-source-content-app-for-magnolia-cms/) and [How to connect elasticsearch with magnolia cms](https://www.andreapagliacci.it/how-connect-elasticsearch-with-magnolia-cms/)

# Installation
For use that app, add dependency in your pom.xml:

    <dependency>
		<groupId>net.seniorsoftwareengineer</groupId>
		<artifactId>magnolia-cms-content-app-elasticsearch</artifactId>
		<version>1.1</version>
    </dependency>

On settings.xml we must add for authorizing access:
	

    <server>
    		<id>github</id>
    		<username>andreapagliacci</username>
    		<password>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</password>
    </server>

and settings url where are saved package:
	

    <repository>
    		<id>github</id>
    		<url>https://maven.pkg.github.com/magnolia-community/elasticsearch-s3-content-app-for-magnolia</url>
    		<releases>
    			<enabled>true</enabled>
    		</releases>
    		<snapshots>
    			<enabled>true</enabled>
    		</snapshots>
    </repository>

Build your solution with:

    mvn install

# Supported Magnolia versions
It tested with Magnolia 6.0 and major versions
