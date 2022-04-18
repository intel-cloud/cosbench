COSBench - Cloud Object Storage Benchmark
=========================================

COSBench is a benchmarking tool to measure the performance of Cloud Object Storage services. Object storage is an
emerging technology that is different from traditional file systems (e.g., NFS) or block device systems (e.g., iSCSI).
Amazon S3 and Openstack* swift are well-known object storage solutions.

COSBench now supports GDAS, E-SuperStor, OpenStack* Swift, Amazon* S3, OpenIO*, Amplidata v2.3, 2.5 and 3.1, Scality*, Ceph, CDMI, Google* Cloud Storage, Aliyun OSS as well as custom adaptors.


New storage: gdas(Usage, please refer to conf/ehualu-config-sample.xml, thanks.)
----------------------------------------
- GDAS is S3 storage and hardware is Blu-ray Disc.
- mprepare, multipart upload object at prepare stage.
- mfilewrite, multipart upload object at filewrite stage.
- aws_region parameter for gdas.
- Head object
- GiB, MiB, KiB: Now GB is 10^n, GiB is 2^n.  
- Multipart upload: Add Multipart upload method and part_size parameter: You can set it now. Default is 5MiB.  
- Restore Object: Add Restore method and restore_days parameter: restore_days. You can set it now. Default is 1.  
- HTTPS: If want to disable verify SSL, please set no_verify_ssl to true. Default is false.


New storage: ess and essv2(Usage, please refer to conf/ehualu-config-sample.xml, thanks.)
----------------------------------------
- ESS is e-SuperStor and hardware can be 'fullstack' (NVMe, SSD, HDD, Tape, Disc, etc.). 
- mprepare, multipart upload object at prepare stage.
- mfilewrite, multipart upload object at filewrite stage.
- aws_region parameter for essv2.
- New feature from bissenbay/s3-range-and-prefetch, thanks for this PR(only for ess, please read conf/s3-config-prefetch-sample.xml and conf/s3-config-range-sample.xml).
- Head object
- GiB, MiB, KiB: Now GB is 10^n, GiB is 2^n.  
- Multipart upload: Add Multipart upload method and part_size parameter: You can set it now. Default is 5MiB.  
- Restore Object: Add Restore method and restore_days parameter: restore_days. You can set it now. Default is 1.  
- StorageClass: Now you can set object's storageclass. Default is STANDARD.
- HTTPS: If want to disable verify SSL, please set no_verify_ssl to true. Default is false.


eHualu - Notice
----------------------------------------
- s3v2 was deleted(essv2 instead), s3 is no longer actively maintained.
- if need new features, please use ess/essv2, thanks.
- ess means e-SuperStor(s3)
- ess use aws-sdk-java(now, version is 1.12.158)
- essv2 use aws-sdk-java-v2(now, version is 2.17.129)


Important Notice and Contact Information
----------------------------------------

a) COSBench is not a product, and it does not have a full-time support team. Before you use this tool, please understand 
the need to invest enough effort to learn how to use it effectively and to address possible bugs.

b) To help COSBench develop further, please become an active member of the community and consider giving back by making
contributions.


Licensing
---------

a) Intel source code is being released under the Apache 2.0 license.

b) Additional libraries used with COSBench have their own licensing; refer to 3rd-party-licenses.pdf for details.


Distribution Packages
---------------------

Please refer to "DISTRIBUTIONS.md" to get the link for distribution packages.


Installation & Usage
--------------------

Please refer to "COSBenchUserGuide.pdf" for details.


Adaptor Development
-------------------
If needed, adaptors can be developed for new storage services; please refer to "COSBenchAdaptorDevGuide.pdf" for details.


Build
-----
If a build from source code is needed, please refer to BUILD.md for details.


Resources
---------

Wiki: (https://github.com/intel-cloud/cosbench/wiki)

Issue tracking: (https://github.com/intel-cloud/cosbench/issues)

Mailing list: (http://cosbench.1094679.n5.nabble.com/)


*Other names and brands may be claimed as the property of others.


Other related projects
----------------------
COSBench-Workload-Generator: (https://github.com/giteshnandre/COSBench-Workload-Generator)

COSBench-Plot: (https://github.com/icclab/cosbench-plot)

COSBench-Appliance: (https://susestudio.com/a/8Kp374/cosbench)

COSBench Ansible Playbook:

- (http://www.ksingh.co.in/blog/2016/05/29/deploy-cosbench-using-ansible/)
- (https://github.com/ksingh7/ansible-role-cosbench)
- (https://galaxy.ansible.com/ksingh7/cosbench/)


= END =
