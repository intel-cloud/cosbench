<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" type="text/css" href="resources/cosbench.css" />
  <title>Workload Configuration</title>
  <script>
	  function toggleDiv(id)
		{
			divElement = document.getElementById(id);
			if(divElement.style.display == 'none'){
				divElement.style.display = '';
			}else {
				divElement.style.display = 'none';
			}
			return false;
		}
  </script>
</head>
<body>
<#include "header.ftl">
<div id="main">
<div class="top"><br /></div>
<div class="content">
  <h3>Workload Configuration</h3>
  <h5>(The page only provides basic configuration options, for advanced options, please follow user guide and edit xml files directly.)</h5>
  <div>
    <form action="config-workload.do" method="post" class="content" >
	  <#if error?? >
        <p><span class="error"><strong>Note</strong>: ${error}!</span></p>
      </#if>
      
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
								<th ></th>
								<th >Type</th>
								<th >Configuration</th>
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
					</table>
			</div>
		</div>
	
		<div id="workflow" class="a1">
			<h3>Workflow</h3>
			
			<div id="init" class="a2">
					<input type="checkbox" name="init.checked" checked="checked" onClick="toggleDiv('init.work');"><strong> Init Stage: </strong>
				
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
			</div>
			
			<div id="prepare" class="a2">
					<input type="checkbox" name="prepare.checked" checked="checked" onClick="toggleDiv('prepare.work');"><strong> Prepare Stage:</strong>
				
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
										  <option value="GB">GB</option>
										</select>
									</td>
								</tr>
							</tbody>							
						</table>
				</div>
			</div>
			
			<div id="normal" class="a2">
					<input type="checkbox" name="normal.checked" checked="checked" onClick="toggleDiv('normal.work');"><strong> Main Stage:</strong>
				
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
			</div>
			
			<div id="cleanup" class="a2">
					<input type="checkbox" name="cleanup.checked" checked="checked" onClick="toggleDiv('cleanup.work');"><strong> Cleanup Stage:</strong>
				
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
			</div>
			
			<div id="dispose" class="a2">
					<input type="checkbox" name="dispose.checked" checked="checked" onClick="toggleDiv('dispose.work');"><strong> Dispose Stage:</strong>
				
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
			</div>
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
</body>  
</html>