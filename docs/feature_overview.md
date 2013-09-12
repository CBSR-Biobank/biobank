# BioBank Feature Overview

This document describes the features provided by BioBank. BioBank is a client server application
using an N-tier server architecture. A Thick Client, used by clinic personnel and repository site
technicians, provides the functionality for specimen collection and processing.

The top level features of the software are:

1. Collection protocol definition
2. Specimen processing
3. Specimen ordering for researchers
4. Repository site configuration management
5. Reports

These features are discussed in more details in the following Sections

### COLLECTION PROTOCOLS

Collection protocols in BioBank are referred to as Studies and multiple studies can be
defined. Studies can be configured to receive biological specimens from multiple clinics. Studies
and clinics are linked via a contact person. The specimen processing ensures that specimens are
received from clinics associated with a study.

A study can also be configured with the specimen types it is to receive. Specimen processing
generates warnings if the technician adds a specimen type not defined by the study. The specimen
types can also be configured to require time drawn and original volumes.

A study also defines the valid aliquoted and derived specimens that it will collect. Default volumes
and the quantity of aliquots / derivatives can also be specified. A study can add or remove the
aliquots / derivatives at any time.

The study can define the patient information to be collected on each visit. This information can be
also updated / changed during the course of the study.

### SPECIMEN PROCESSING

Specimen processing is a two stage process that involves nurses / technicians at the collecting
clinic and technicians at the repository site. First the nurse / technician at the collecting clinic
creates a patient record in the database for the patient being processed. The patient is referred to
by a unique identifier and no personal information is stored by Biobank. Once the patient has been
added to the system, a collection event is created for that patient. The collection event specifies
the specimens that were collected and any additional information specified by the study.

The clinic nurse / technician then creates a dispatch which specifies the specimens to be shipped to
the repository site. The dispatch can be made of the specimens for one or more patients. Essentially
a dispatch records the manifest of what is being shipped to the repository site. Dispatches are
usually shipped to repository sites, but the software is flexible in that if necessary a dispatch
can be sent from one clinic to another. A dispatch is a record of specimens sent from an originating
clinic or site to a destination clinic or site.

It is also possible that a clinic may aliquot or create derivatives for specimens. A dispatch can
also hold these specimens if the clinic wishes to send them to a site.

Once a package is ready for shipping, the clinic nurse / technician can then enter shipping
information for the package (shipping method and waybill for example). At this point the repository
site technicians will see that the package is in transit to their site.

Once the package arrives at the repository site, the site technician validates the manifest with the
aid of the software. If all specimens are contained and there is no problem regarding the validity
of the specimens they can now be processed. A processing event is now created for the shipment and
some or all of the specimens received in the dispatch are added. The specimens are added by scanning
the 2D Data Matrix barcode on the specimen tube. After the processing event is created, the
technician can then aliquot or create derivative specimens (physical step).

The next step in processing is to link the aliquoted / derived specimens to the source specimens
they come from. This is done with the Specimen Link feature in the software. This feature allows the
user to link NUNC tubes, with laser etched 2D Data Matrix barcodes, in bulk or individual
tubes. This feature performs validations at each step to minimize human error. A flatbed scanner can
be used when handling NUNC tubes. Currently the flatbed scanning can only be done with the software
running under Microsoft Windows.

The last step in processing is to assign the aliquoted / derived specimens a storage location. This
is done with the Specimen Assign link feature which is similar to the specimen link feature. The
feature performs error checking to ensure that valid container locations are selected for the
specimen types being stored.

The specimen link and specimen assign features give the user the option of printing a log of what
was done for record keeping purposes.


### SPECIMEN ORDER

The specimen order web client feature allows researchers or members of a research group to request
aliquots and / or derived specimens from their studies. All order requests can be configured to
require the approval from a principal investigator prior to filling an order. Specimens are ordered
by patient visit number and specimen type. Orders can be created via a web interface or by uploading
an input file.  The package the specimens are stored in can be delivered to an address specified by
the research group member.

The software will allow for research groups to be defined and authorizations given to group
members. Research groups can be associated with one or more studies.

### REPOSITORY SITE CONFIGURATION

In the BioBank Thick Client, the following can be configured for repository sites: studies, clinics,
and storage. Sites can be configured to participate in a subset of the studies defined in the
system. Sites can be configured to define the subset of clinics that can send dispatches to
them. Storage containers can be configured to be of any dimension and container hierarchies can be
defined. Containers can also define the specimen types that they will hold.

### REPORTS

The Biobank Thick Client supports 2 types of reports: hard coded and user defined. Hard coded
reports are some of the reports currently required by CBSR. For example, some of the reports are
patients per clinic per study, patient visits per patient per study, etc. With user defined reports
the user can specify what he /she would like to see by selecting from the various tables in the
database.

### FUTURE DEVELOPMENT

A Web Client will provide functionality to researchers and administrators. Researchers will be able
to order specimens that have been stored for them based on criteria they define. Administrators will
be able to generate reports detailing the operation of repository sites.

### BIOBANK THICK CLIENT REQUIREMENTS

The client software runs on Microsoft Windows 7, Microsoft Windows XP, Mac OSX, and Linux. The
Flatbed scanning feature is only available on Microsoft Windows 7 and Microsoft Windows XP. The
computer running the client needs a working internet connection. On Microsoft Windows the client
comes bundled with it’s own version of the Java Runtime Environment and will not interfere with a
previously installed version of Java.

### BIOBANK SERVER REQUIREMENTS

To run a BioBank server the following is required:

<table>
  <tr>
    <th>Software</th>
    <th>Version</th>
    <th>Notes</th>
  </tr>
  <tr>
    <td>Ubuntu Linux</th>
    <td>10.04 or later</th>
    <td>Operating System</th>
  </tr>
  <tr>
    <td>JBoss</th>
    <td>4.0.5</th>
    <td>Application Server</th>
  </tr>
  <tr>
    <td>JDK</th>
    <td>1.6</th>
    <td>Java</th>
  </tr>
  <tr>
    <td>MySQL</th>
    <td>5.1</th>
    <td>Database Server</th>
  </tr>
<table>

If you are using BioBank in association with The Canadian BioSample Repository (CBSR) you will not
need to run your own version of the BioBank server.

FURTHER INFORMATION

For further information you may email The Canadian BioSample Repository (CBSR) at
hel@biosample.ca. You may also like to visit the web site at:

* [http://biosample.ca/](http://biosample.ca/)

