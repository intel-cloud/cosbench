TODO
----
COSBench is an ongoing project, in next six months, we plan to make below improvements:

1. more storage interface support, e.g. s3 and cdmi

2. async http request

3. usability enhancement.

4. document improvement


Known Issues
------------
COSBench has been evaluated by a few early users before open source, below are known issues:

Issue # PRIORITY	CATEGORY	DESCRIPTION	Comments	Action	Final Disposition
1	Medium	Environment	"The tool must be able to reclaim disk space in under 4 hours between test runs to prevent storage devices from being filled. (Default background garbage collection may take 2hours -10 days or longer… duration varies by system load)
- faster data deletion"	. Add explanation why disk space is not release even object is deleted? For amplidata	Documentation	Yaguang



 
If you have any other suggestions or you want to work with us together. please contact us at yaguang.wang@intel.com.


== END ==
