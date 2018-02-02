# RiverForecast
The RiverForecast Add-On to WebCTRL polls river forecast data from the California
Nevada River Forecast Center (http://www.cnrfc.noaa.gov) and extracts selected
data points for use in control programs. The following kinds of data are currently 
supported: 

* Forecasted river level and flow
* Observed river level and flow
* Critical level thresholds for danger, flood, and monitor

Project Status
--------------
This project is provided as a teaching example of how you might retrieve a 
file from a network source, parse and extract data from it, and write that 
data to a microblock for control programs to use.

This is not a feature rich implementation, and it is not an ALC supported add-on.
Please check the code and comments for specific TODO items and shortcomings.

Things you might learn from this example:

* Basic add-on development
* How to use the direct-access api to run read and write actions
* How to use the datastore api
* How to retrieve and parse an xml file
* How to set up a thread for periodic execution

Support and Other Downloads
---------------------------
The [ALCshare.com](http://www.alcshare.com) site should be used for discussion 
and community support for this add-on.
