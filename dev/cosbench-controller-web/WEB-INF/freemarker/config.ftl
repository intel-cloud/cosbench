<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" type="text/css" href="resources/cosbench.css" />
  <title>Workload Configuration</title>
</head>
<body>
<#include "header.ftl">
<div id="main">
<div class="top"><br /></div>
<div class="content">
  <h3 id="Wconfig">Workload Configuration</h3>
   <span><p id="Wconfig1"> Workload Description</span></p>
  <script>
  document.getElementById("Wconfig1").onmouseover = function() {mouseOver()};
  document.getElementById("Wconfig1").onmouseout = function() {mouseOut()};
    function mouseOver(){
   document.getElementById("Wconfig1").innerHTML = "The page only provides basic configuration options, for advanced options, please follow user guide and edit xml files directly.";

  }
  
   function mouseOut(){
   document.getElementById("Wconfig1").innerHTML = "Workload Description";
  document.getElementById("Wconfig1").style.color = "black";
  }
  </script>

  </div>
  
 
  <div>
    <form action="config-workload.do" method="post" class="content" >
	  <#if error?? >
        <p><span class="error"><strong>Note</strong>: ${error}!</span></p>
      </#if>
   <div 
    <component
    id="workload.prop" table class="info-table"
			onClick="toggleStageDiv(0,this.parentNode)
			description="Hover"
			</component> 
			</div>
			
		<div id="workload" class="a1">
			<h3>Workload</h3>	
			
			<div id="workload.prop" class="a2">
					<table class="info-table">
						<thead>
							<tr>
								<th>Name</th>
								<th>Description</th>
							</tr>
						</thead>
						
						<tbody>
							<tr>
								<td>
									<input name="workload.name" type="text" style="width:100px" value="test" />
								</td>								
								<td>
									<input name="workload.desc" type="text" style="width:500px" value="sample workload configuration" />
								</td>							
							</tr>
						
						</tbody>
					</table>
			</div>
			
			<div id="workload.as" class="a2">
					<table class="info-table">
						<thead>
						
							<tr>
							
								<th ><strong></strong> </th>
								<th ><strong>Type</strong></a> </th>
								<th ></strong>Configuration</strong></a> </th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td >Authentication</td>
								<td >
									<select name="auth.type">
									  <option value="swauth" selected="true">swauth</option>
									  <option value="keystone">keystone</option>
									  <option value="mock">mock</option>
									  <option value="none">none</option>
									</select>
								</td>
								<td >
									<input name="auth.config" type="text" style="width:500px" value="username=test:tester;password=testing;url=http://192.168.10.1:8080/auth/v1.0" 
					title="different auth system has different parameters: &#10;[swauth]: username=<account:username>;password=<password>;url=<url> &#10;[keystone]: username=<account:username>;password=<password>;url=<url> &#10;[mock]: delay=<time> &#10;[none]: " /> 
								</td>
							</tr>
							
							<tr>
								<td >Storage</td>
								<td >
									<select name="storage.type">
									  <option value="swift" selected="true">swift</option>
									  <option value="ampli">amplistor</option>
									  <option value="mock">mock</option>
									  <option value="none">none</option>
									</select>
								</td>
								<td >
									<input name="storage.config" type="text" style="width:500px" value=""
					title="different storage system has different parameters: &#10 [swift]:  &#10 [ampli]: host=<host>;port=<port>;nsroot=<namespace root>;policy=<policy id> &#10; [mock]: delay=<time>;&#10 [none]: " /> 
								</td>
							</tr>
						</tbody>
						
<!--						
						<tbody>
							<tr>
								<td >Authentication</td>
								<td >
									<select name="auth.type" id="auth.type" onChange="changeAuth()">
									  <option value="swauth" selected="true">swauth/tempauth</option>
									  <option value="keystone">keystone</option>
									  <option value="httpauth">basic/digest</option>									
									  <option value="mock">mock</option>
									  <option value="none">none</option>
									</select>
								</td>
								<td >
									<input name="auth.config" id="auth.config" type="text" style="width:500px" value="username=<username>;password=<password>;auth_url=<url>"
									title="different auth system has different parameters: &#10;[swauth]: username=<account:username>;password=<password>;url=<url> &#10;[keystone]: username=<account:username>;password=<password>;url=<url> &#10;[mock]: delay=<time> &#10;[none]: " /> 
					
								</td>
							</tr>
							
							<tr>
								<td >Storage</td>
								<td >
									<select name="storage.type" id="storage.type" onChange="changeStorage()">
									  <option value="swift" selected="true">Swift</option>
									  <option value="ampli">Amplistor</option>
									  <option value="s3">S3</option>
									  <option value="librados">Ceph LibRados</option>
									  <option value="cdmi">CDMI</option>
									  <option value="cdmi_swift">CDMI for Swift</option>
									  <option value="mock">mock</option>
									  <option value="none">none</option>
									</select>
								</td>
								<td >
									<input name="storage.config" id="storage.config" type="text" style="width:500px" value="" 
									title="different storage system has different parameters: &#10 [swift]:  &#10 [ampli]: host=<host>;port=<port>;nsroot=<namespace root>;policy=<policy id> &#10; [mock]: delay=<time>;&#10 [none]: " /> 
					
								</td>
							</tr>
						</tbody>
-->
					</table>
			</div>
		</div>
	
		<div id="workflow" class="a1">
			<h3>Workflow</h3>
			
			<div id="init" class="a2">
					
					<input type="checkbox" id="init.checked" name="init.checked" checked="checked" onClick="toggleStageDiv(0,this.parentNode);">
					<a href=" " title="&#10 Init Table is used to create specific containers in bulk.
					                   &#10 Parameters : containers, container prefix and suffix . 
					                   &#10 Following format - 
					                   &#10 <work type=init workers=4 config=containers=r(1,100)>">
					                   <strong> Init Stage: </strong></a>
          	
				
				<div id="init.work" class="a3">
						<table class="info-table">
							<thead>
								<th>Worker Count</th>
								<th>Container Selector</th>
							</thead>
							
							<tbody>
								<tr>
									<td>
										<input name="init.workers" type="number" style="width:30px" value="1"/>
									</td>

									<td>
										<select name="init.containers" hidden="true">
										  <option value="r" selected="true">Range</option>
										</select>
										<input name="init.containers.min" type="number" style="width:30px" value="1" /> - <input name="init.containers.max" type="number" style="width:30px" value="32" />
									</td>
								</tr>
							</tbody>
						</table>
				</div>
				
				<div id="init.delay" class="a2">
					<input type="checkbox" name="init.delay.checked"  onClick="toggleDiv(document.getElementById('init.delay.work'));"> 
			<strong> Delay: </strong>
        
          			<div id="init.delay.work" class="a3" style="display:none">
            			<table class="info-table">
		              			<thead>
		               				<th>closuredelay</th>
		              			</thead>
		              
		              			<tbody>
			                		<tr>
			                  			<td>
			                    			<input name="init.delay.closuredelay" type="number" style="width:30px" value="60"/>
			                  			</td>
			                		</tr>
		              			</tbody>
            			</table>
          			</div>
        		</div> 
			</div>
			<input type="button" id="addinit" value="Add Init Stage" onClick="addStage(0);" />
			
			<div id="prepare" class="a2">
			
					<input type="checkbox" id="prepare.checked" name="prepare.checked" checked="checked" onClick="toggleStageDiv(1,this.parentNode);"><a href=" 
					" title="&#10 Prepare Table is used to create specific objects in bulk. 
					         &#10 Following format -
					         &#10 <work type=prepare workers=4 config=containers=r(1,100);objects=r(1,100);sizes=c(64)KB>"><strong>Prepare Stage:</strong></a> 			
				
				<div id="prepare.work" class="a3">
						<table class="info-table">
							<thead>
								<th>Worker Count</th>
								<th>Container Selector</th>
								<th>Object Selector</th>
								<th>Size Selector</th>
							</thead>
							
							<tbody>
								<tr>
									<td>
										<input type="number" name="prepare.workers" style="width:30px" value="1" />
									</td>
									
									<td>
										<select name="prepare.containers" hidden="true">
										  <option value="r" selected="true">Range</option>
										</select>
										<input type="number" name="prepare.containers.min" style="width:30px" value="1"/> - <input type="number" name="prepare.containers.max" style="width:30px" value="32" />
									</td>
									<td>
										<select name="prepare.objects" hidden="true">
										  <option value="r" selected="true">Range</option>
										</select>
										<input type="number" name="prepare.objects.min" style="width:30px" value="1"/> - <input type="number" name="prepare.objects.max" style="width:30px" value="50"/>
									</td>
									<td>									
										<select name="prepare.sizes" hidden="true">
										  <option value="u" selected="true">Uniform</option>
										</select>
										<input type="number" name="prepare.sizes.min" style="width:30px" value="64"/> - <input type="number" name="prepare.sizes.max" style="width:30px" value="64"/>
										<select name="prepare.sizes.unit">
										  <option value="B">Byte</option>
										  <option value="KB" selected="true">KB</option>
										  <option value="MB">MB</option>
										  <option value="GB">GB</option>	</select>
									</td>
								</tr>
							</tbody>							
						</table>
				</div>
				<div id="prepare.delay" class="a2">
						<input type="checkbox" name="prepare.delay.checked" onClick="toggleDiv(document.getElementById('prepare.delay.work'));"><strong> Delay: </strong>

						<div id="prepare.delay.work" class="a3" style="display:none">
							<table class="info-table">
								<thead>
									<th>closuredelay</th>
								</thead>
							
								<tbody>
									<tr>
										<td>
											<input name="prepare.delay.closuredelay" type="number" style="width:30px" value="60"/>
										</td>
									</tr>
								</tbody>
							</table>
						</div>
				</div>
			</div>
			<input type="button" id="addprepare" value="Add Prepare Stage" onClick="addStage(1);" />
			
			<div id="normal" class="a2">
					<input type="checkbox" id="normal.checked" name="normal.checked" checked="checked" onClick="toggleStageDiv(2,this.parentNode);">
					<a href=" " title="&#10 Main stage contains attributes like runtime and rampup which provide the ending options"><strong> Main Stage:</strong></a>
									
				
				<div id="normal.work" class="a3">
						<table class="info-table">
							<thead>
								<th>Worker Count</th>
								<th>Rampup Time (in second)</th>
								<th>Runtime (in second)</th>
							</thead>
							
							<tbody>
								<tr>
									<td>
										<input type="number" name="normal.workers" style="width:30px" value="8" /> 
									</td>
									<td>
										<input type="number" name="normal.rampup" style="width:30px" value="100" /> 
									</td>
									<td>
										<input type="number" name="normal.runtime" style="width:30px" value="300" /> 
									</td>
								</tr>
							
							</tbody>
						</table>
					<div id="normal.op" class="a4">
						<p>
							<table class="info-table">
								<thead>
									<tr>
										<th >Operation</th>
										<th >Ratio</th>
										<th >Container Selector</th>
										<th >Object Selector</th>
										<th >Size Selector</th>
										<th >File selector</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td >Read</td>
										<td ><input type="number" name="read.ratio" style="width:30px" value="80" /> </td>
										<td >
											<select name="read.containers" hidden="true">
											  <option value="u" selected="true">Uniform</option>
											</select>
											<input type="number" name="read.containers.min" style="width:30px" value="1" /> - <input type="number" name="read.containers.max" style="width:30px" value="32" /> 
										</td>
										<td >
											<select name="read.objects" hidden="true">
											  <option value="u" selected="true">Uniform</option>
											</select>
											<input type="number" name="read.objects.min" style="width:30px" value="1" /> - <input type="number" name="read.objects.max" style="width:30px" value="50" /> 
										</td>
																	
									</tr>
									<tr>
										<td >Write</td>
										<td ><input type="number" name="write.ratio" style="width:30px" value="20" /> </td>
										<td >
											<select name="write.containers" hidden="true">
											  <option value="u" selected="true">Uniform</option>
											</select>
											<input type="number" name="write.containers.min" style="width:30px" value="1" /> - <input type="number" name="write.containers.max" style="width:30px" value="32" /> 
										</td>
										<td >
											<select name="write.objects" hidden="true">
											  <option value="u" selected="true">Uniform</option>
											</select>
											<input type="number" name="write.objects.min" style="width:30px" value="51"/> - <input type="number" name="write.objects.max" style="width:30px" value="100" /> 
										</td>
										<td >
											<select name="write.sizes" hidden="true">
											  <option value="u" selected="true">Uniform</option>
											</select>
											<input type="number" name="write.sizes.min" style="width:30px" value="64"/> - <input type="number" name="write.sizes.max" style="width:30px" value="64"/>
											<select name="write.sizes.unit">
											  <option value="B">Byte</option>
											  <option value="KB" selected="true">KB</option>
											  <option value="MB">MB</option>
											  <option value="GB">GB</option>
											</select>										
										</td>															
									</tr>
									<tr>                                        
                                        <td >File-Write</td>
										<td ><input type="number" name="filewrite.ratio" style="width:30px" value="0" /> </td>
										<td >
											<select name="filewrite.containers" hidden="true">
											  <option value="u" selected="true">Uniform</option>
											</select>
											<input type="number" name="filewrite.containers.min" style="width:30px" value="1" /> - <input type="number" name="filewrite.containers.max" style="width:30px" value="32" /> 
										</td>
										<td >
											<select name="filewrite.fileselection" hidden="true">
											  <option value="s" selected="true">Uniform</option>
											</select>
										</td>
                                        <td >
                                        </td>
										<td >
											<input name="filewrite.files" type="text" style="width:100px" value="/tmp/testfiles/" />									
										</td>															
									</tr>
									<tr>
									<tr>
										<td >Delete</td>
										<td ><input type="number" name="delete.ratio" style="width:30px" value="0"/> </td>
										<td >
											<select name="delete.containers" hidden="true">
											  <option value="u" selected="true">Uniform</option>
											</select>
											<input type="number" name="delete.containers.min" style="width:30px" value="1"/> - <input type="number" name="delete.containers.max" style="width:30px" value="100"/> 
										</td>
										<td >
											<select name="delete.objects" hidden="true">
											  <option value="u" selected="true">Uniform</option>
											</select>
											<input type="number" name="delete.objects.min" style="width:30px" value="1"/> - <input type="number" name="delete.objects.max" style="width:30px" value="100"/> 
										</td>
																									
									</tr>
								</tbody>
							</table>
						</p>
					</div>
				</div>
					<div id="normal.delay" class="a2">
							<input type="checkbox" name="normal.delay.checked" onClick="toggleDiv(document.getElementById('normal.delay.work'));"><strong> Delay: </strong>
						
							<div id="normal.delay.work" class="a3" style="display:none">
									<table class="info-table">
										<thead>
											<th>closuredelay</th>
										</thead>
						
										<tbody>
											<tr>
												<td>
													<input name="normal.delay.closuredelay" type="number" style="width:30px" value="60"/>
												</td>
											</tr>
										</tbody>
									</table>
							</div>
					</div>
			</div>
			<input type="button" id="addnormal" value="Add Main Stage" onClick="addStage(2);" /> 
			
			<div id="cleanup" class="a2">
			
					<input type="checkbox" id="cleanup.checked" name="cleanup.checked" checked="checked" onClick="toggleStageDiv(3,this.parentNode);"><a href="" 
					title="&#10 Cleanup Table is used to remove specific objects in bulk.
					       &#10 Following format -
					       &#10 <work type=cleanup workers=4 config=containers=r(1,100);objects=r(1,100)>""><strong> Cleanup Stage:</strong></a> 
				
				<div id="cleanup.work" class="a3">
						<table class="info-table">
							<thead>
								<th>Worker Count</th>
								<th>Container Selector</th>
								<th>Object Selector</th>
							</thead>
							
							<tbody>
								<tr>
									<td>
										<input type="number" name="cleanup.workers" style="width:30px" value="1" />
									</td>
									
									<td>
										<select name="cleanup.containers" hidden="true">
										  <option value="r" selected="true" >Range</option>
										</select>
										<input type="number" name="cleanup.containers.min" style="width:30px" value="1" /> - <input type="number" name="cleanup.containers.max" style="width:30px" value="32" />
									</td>	
									
									<td>
										<select name="cleanup.objects" hidden="true">
										  <option value="r" selected="true" >Range</option>
										</select>
										<input type="number" name="cleanup.objects.min" style="width:30px" value="1"/> - <input type="number" name="cleanup.objects.max" style="width:30px" value="100"/>
									</td>	
								</tr>					
							</tbody>							
						</table>
				</div>
				<div id="cleanup.delay" class="a2">
					<input type="checkbox" name="cleanup.delay.checked" onClick="toggleDiv(document.getElementById('cleanup.delay.work'));"><strong> Delay: </strong>
					
					<div id="cleanup.delay.work" class="a3" style="display:none">
							<table class="info-table">
								<thead>
									<th>closuredelay</th>
								</thead>
					
								<tbody>
									<tr>
										<td>
											<input name="cleanup.delay.closuredelay" type="number" style="width:30px" value="60"/>
										</td>
									</tr>
								</tbody>
							</table>
					</div>
				</div>
			</div>
			<input type="button" id="addcleanup" value="Add Cleanup Stage" onClick="addStage(3);" />
			
			<div id="dispose" class="a2">
			<input type="checkbox" id="dispose.checked" name="dispose.checked" checked="checked" onClick="toggleStageDiv(4,this.parentNode);"><a href=" 
			" title="&#10 Dispose Table is used to remove specific containers in bulk. 
			         &#10 Following format - 
			         &#10 <work type=dispose workers=4 config=containers=r(1,100)/>""><strong> Dispose Stage:</strong></a> 
			
				
				<div id="dispose.work" class="a3">
						<table class="info-table">
							<thead>
								<th>Worker Count</th>
								<th>Container Selector</th>
							</thead>
							
							<tbody>
								<tr>
									<td>
										<input type="number" name="dispose.workers" style="width:30px" value="1" />
									</td>									
									<td>
										<select name="dispose.containers" hidden="true">
										  <option value="r" selected="true">Range</option>
										</select>
										<input type="number" name="dispose.containers.min" style="width:30px" value="1" /> - <input type="number" name="dispose.containers.max" style="width:30px" value="32"/>
									</td>
								</tr>							
							</tbody>
						</table>
				</div>
					<div id="dispose.delay" class="a2">
							<input type="checkbox" name="dispose.delay.checked" onClick="toggleDiv(document.getElementById('dispose.delay.work'));"><strong> Delay: </strong>
							
							<div id="dispose.delay.work" class="a3" style="display:none">
									<table class="info-table">
										<thead>
											<th>closuredelay</th>
										</thead>
						
										<tbody>
											<tr>
												<td>
														<input name="dispose.delay.closuredelay" type="number" style="width:30px" value="60"/>
												</td>
											</tr>
										</tbody>
									</table>
							</div>
					</div>
			</div>
			<input type="button" id="adddispose" value="Add Dispose Stage" onClick="addStage(4);" />
		</div>
		
		<div class="a1">
			<input type="submit" value="Generate Configuration File">
		</div>
	  
    </form>
  </div>
  <p><a href="index.html">go back to index</a></p>
</div> <#-- end of content -->
<div class="bottom"><br /></div>
</div> <#-- end of main -->
<#include "footer.ftl">
<script>
 	var numOfClones = 0;
    var numOfClonesInStage = new Array(5);
    var previousDivs =
    {
		'0' : document.getElementById("init"),
		'1' : document.getElementById("prepare"),
		'2' : document.getElementById("normal"),
		'3' : document.getElementById("cleanup"),
		'4' : document.getElementById("dispose")
    };

    for(var i=0 ; i<5 ; i++)
    {
       	numOfClonesInStage[i]=0;
    }
    
    function toggleDiv(id)
    {
        if (typeof(id) == "string")
            divElement = document.getElementById(id);
        else
            divElement = id;
        
        if (divElement.style.display == 'none')
        {
            divElement.style.display = '';
        }
        else
        {
            divElement.style.display = 'none';
        }
        return false;
    }
    
    function Workloaddesc()
    {
    document.getElementById("Wconfig").innerHTML = "The page only provides basic configuration options, for advanced options, please follow user guide and edit xml files directly.";
    
    }
    
    function toggleStageDiv(stageNum,stageDiv)
    {
        if (numOfClonesInStage[stageNum] == 0)
        {
            toggleDiv(stageDiv.id + '.work');
            toggleDiv(stageDiv.id + '.delay');
            return;
        }
        if (stageDiv.nextElementSibling.id == 'add' + stageDiv.id)
        {
            previousDivs[stageNum] = stageDiv.previousElementSibling;
		}
        numOfClonesInStage[stageNum]--;
        stageDiv.parentNode.removeChild(stageDiv);
    }
    
    function addStage(stageNum)
    {
        if (numOfClonesInStage[stageNum] == 0 && document.getElementById(previousDivs[stageNum].id + '.checked').checked == false)
        {
            return;
        }
        var cloneDiv = previousDivs[stageNum].cloneNode(true);
        previousDivs[stageNum].parentNode.insertBefore(cloneDiv, previousDivs[stageNum].nextElementSibling);
        numOfClonesInStage[stageNum]++;
        previousDivs[stageNum] = cloneDiv;
    } 	
    
    function changeAuth()
    {
    	var select=document.getElementById("auth.type");
		var selected=select.options[select.selectedIndex].value;
		var config=document.getElementById("auth.config");
		
		switch(selected)
		{
			case "swauth":
				config.value="username=<account:username>;password=<password>;auth_url=<url>";
				config.title=" username=test:tester;password=testing;auth_url=http://192.168.0.1:8080/auth/v1.0";
				break;
			case "keystone":
				config.value="username=<username>;password=<password>;auth_url=<url>;service=<service>";
				config.title=" username=tester;password=testing;tenant_name=test;auth_url=http://127.0.0.1:5000/v2.0;service=swift service";
				break;
			case "httpauth":
				config.value="username=<username>;password=<password>;auth_url=<url>";
				config.title=" username=tester;password=testing;auth_url=http://192.168.10.1:8080/cdmi";
				break;
			case "mock":
				config.value="delay=<time>";
				break;		
			case "none":
				config.value="";
				break;	
			default:
				config.value="";
		}				
    }
    
    function changeStorage()
    {
    	var select=document.getElementById("storage.type");
		var selected=select.options[select.selectedIndex].value;
		var config=document.getElementById("storage.config");
		
		switch(selected)
		{
			case "swift":
				config.value="";
				config.title="";
				break;
			case "ampli":
				config.value="host=<host>;port=<port>;nsroot=<namespace root>;policy=<policy id>";
				config.title="where nsroot and policy are optional, but policy is mandatory at init stage."
				break;
			case "s3":
				config.value="accesskey=<accesskey>;secretkey=<scretkey>;proxyhost=<proxyhost>;proxyport=<proxyport>;endpoint=<endpoint>";
				config.title="where proxyhost, proxyport and endpoint are optional.";
				break;
			case "librados":
				config.value="accesskey=<accesskey>;secretkey=<scretkey>;endpoint=<endpoint>";
				config.title="";
				break;		
			case "cdmi":
				config.value="type=<cdmi content type>";
				config.title="where type could be cdmi or non-cdmi to map to cdmi content type and non-cdmi content type.";
				break;			
			case "cdmi_swift":
				config.value="";
				config.title="The storage type is a CDMI extension specially for token-based authentication like Swift.";
				break;
			case "mock":
				config.value="delay=<delay>;size=<object size>;errors=<error rate>;printing=<true|false>;profiling=<true|false>";
				config.title="The storage type is specially used for self-test and demo only, no storage target is required."
				break;
			case "none":
				config.value="";
				config.title="The storage type is specially used to evaluate the program itself's overhead, especially at high volumes.";
				break;	
			default:
				config.value="";
				config.title="";
		}		
	}
 </script>
</body>  
</html>