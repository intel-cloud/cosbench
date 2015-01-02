<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" type="text/css" href="resources/cosbench.css" />
  <title>Workload Matrix Configuration</title>
</head>
<body>
<#include "header.ftl">
<div id="main">
<div class="top"><br /></div>
<div class="content">
  <h3>Workload Matrix Configuration</h3>
    <h5>You can configure workload matrix from here. You can generate or submit workloads directly. With 'Generate Workload File/s' option,
        files will be generated and placed at 'workloads' directory inside COSBench installation directory. No workload files will be generated when
        'Submit Workload/s' option is chosen and workloads will be submitted directly.
        <br>(Note: * marked fields are mandatory.)</br>
    </h5>
  <div>
    <form action="advanced-config-workload.do" method="post" class="content" >
	  <#if error?? >
        <p><span class="error"><strong>Note</strong>: ${error}!</span></p>
      </#if>
      	<label for="workload.matrix.name" ><strong>*</strong>Workload Matrix Name:</label>
        <input id="workload.matrix.name" name="workload.matrix.name" size="15" maxlength="30"/>
      	<div id="workload-common-configuration" class="a1">
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
      	
      	<div id="workload-parameters" class="a2">
      		<table class="info-table">
						<thead>
							<tr>
								<th>Runtime(in secs)</th>
								<th>Rampup(in secs)</th>
								<th>Delay(in secs)</th>
								<th>Number of drivers</th>
							</tr>
						</thead>
						
						<tbody>
							<tr>
								<td>
									<input name="runtime" type="text" style="width:50px" value="300" />
								</td>								
								<td>
									<input name="rampup" type="text" style="width:100px" value="50" />
								</td>
                                <td>
									<input name="delay" type="text" style="width:100px" value="30" />
								</td>
                                <td>
									<input name="num_of_drivers" type="text" style="width:100px" value="1" />
								</td>
							</tr>
						</tbody>
					</table>
      	</div>
      	
		<div id="workload" class="a1">
				
			
			<input id="workload-checkbox" type="checkbox" name="workload.matrix.checked" checked="checked" onClick="toggleDiv(this.nextElementSibling.nextElementSibling.nextElementSibling);" style="float:left">
			<label for="workload-checkbox" class="a2">*Workload Name:</label>
			<input name="workload.name" size="15" maxlength="30"/>
			<div name="workload.matrix" id="workload.matrix" class="a2" >
					<input type="hidden" id="workload-number" name="workload-number" value="0">
					<table class="info-table">
						<thead>
							<tr>
								<th>Object sizes </th>
								<th>Object size unit </th>
								<th>Objects per container </th>
                                <th>Containers </th>
                                <th>Workers </th>
							</tr>
						</thead>
						
						<tbody>
							<tr>
								<td>
									<input name="object-sizes" type="text" style="width:150px" value="4,128,512" />
								</td>
								<td>
									<select name="object-size-unit">
										  <option value="B">Byte</option>
										  <option value="KB" selected="true">KB</option>
										  <option value="MB">MB</option>
										  <option value="GB">GB</option>
									</select>
								</td>								
								<td>
									<input name="num-of-objects" type="text" style="width:100px" value="1000" />
								</td>
                                <td>
									<input name="num-of-containers" type="text" style="width:100px" value="1,100" />
								</td>
                                <td>
									<input name="workers" type="text" style="width:100px" value="1,2,4,16,32,64" />
								</td>
                                
							</tr>
						
						</tbody>
					</table>
					
					<div name="read.write.delete.matrix" id="read.write.delete.matrix" class="a2" >
					<input type="checkbox" name="read.write.delete.matrix.checked" checked="checked" onClick="toggleDiv(this.nextElementSibling);" style="float:left">
					<table class="rwd-table">
						<thead>
							<tr>
							    <th>Read % </th>
                                <th>Write % </th>
                                <th>Delete % </th>
							</tr>
						</thead>
						
						<tbody>
							<tr>
					            <td>
									<input name="read-ratio0" type="text" style="width:100px" value="80" />
								</td>
                                <td>
									<input name="write-ratio0" type="text" style="width:100px" value="15" />
								</td>
                                <td>
									<input name="delete-ratio0" type="text" style="width:100px" value="5" />
								</td>	
                    		</tr>
						
						</tbody>
					</table>
					</div>
					
					<input type="button" id="addRWDMatrixLine" value="Add RWD Ratio" onClick="addRWDMatrixRow(this.previousElementSibling);" />
					
					<br><br>
					<a  href="javascript:void(0);" onclick="deleteMatrixRow(this.parentNode.parentNode);"> remove workload </a>
			</div>
			
		</div>
		
		<div class="a2">
			<input type="button" id="addMatrixLine" value="Add Workload" onClick="addMatrixRow();" />
		</div>
		
		<div class="a1">
			<br><br><br><br>
			<input name='generate-workload' type="submit" value="Generate Workload File/s">
			<br><br>
			OR
			<br><br>
			<input name='submit-workload' type="submit" value="Submit Workload/s" style="width=500px">
		</div>
    </form>
        
  </div>
<p><a href="index.html">go back to index</a></p>
</div> <#-- end of content -->
<div class="bottom"><br /></div>
</div> <#-- end of main -->
<#include "footer.ftl">
<script>

	var rwdDivCount = 1;
	var workloadDivCount=1;
	var previousWorkloadDiv=document.getElementById('workload');
	var firstWorkloadCloneDiv = previousWorkloadDiv.cloneNode(true);
	
	var previousRWDRatioDiv=document.getElementById('read.write.delete.Matrix');
	var firstRWDRatioCloneDiv = previousRWDRatioDiv.cloneNode(true);
	
	
	function deleteMatrixRow(divElement) 
	{
    	previousWorkloadDiv = divElement.previousElementSibling;
        divElement.parentNode.removeChild(divElement);
	}

	function addMatrixRow()
	{
	    var cloneDiv = firstWorkloadCloneDiv.cloneNode(true); 
	   	setRWDElementNames(getRWDMatrixElement(cloneDiv));
	   	setWorkloadNumberElement(getWorkloadNumberElement(cloneDiv));
	    previousWorkloadDiv.parentNode.insertBefore(cloneDiv, previousWorkloadDiv.nextElementSibling);
	    previousWorkloadDiv = cloneDiv;
	}
	
	function getWorkloadNumberElement(divElement)
	{
		var insideDivs = divElement.getElementsByTagName('div');
		var inputDivs = insideDivs[0].getElementsByTagName('input');
		return inputDivs[0];
	}
	
	function getRWDMatrixElement(divElement)
	{
		var workloadMatrixElements = divElement.getElementsByTagName('div');
		var rwdMatrixElements = workloadMatrixElements[0].getElementsByTagName('div');
		return rwdMatrixElements[0];
	}
	
	function addRWDMatrixRow(divElement)
	{
	    var cloneDiv = divElement.cloneNode(true);
	    previousElement = divElement.previousElementSibling;
	    insertAfter(cloneDiv,previousElement);
	}
	
	 function toggleDiv(divElement)
	{
        if(divElement.style.display == 'none'){
        divElement.style.display = '';
        toggleDisabled(divElement);
        }else {
        divElement.style.display = 'none';
        toggleDisabled(divElement);
        }
        return false;
	}
	
	function toggleDisabled(element) 
	{
        try {
            element.disabled = element.disabled ? false : true;
        }
        catch(E){}
        
        if (element.childNodes && element.childNodes.length > 0) {
            for (var i = 0; i < element.childNodes.length; i++) {
                toggleDisabled(element.childNodes[i]);
            }
        }
     }
	
	function insertAfter(newElement,targetElement) {
    
    var parent = targetElement.parentNode;

    if(parent.lastchild == targetElement) {
        parent.appendChild(newElement);
        } else {
        parent.insertBefore(newElement, targetElement.nextSibling);
        }
	}
	
	function setRWDElementNames(divElement) {
		var rwdElements = divElement.getElementsByTagName('input');
		rwdElements[1].name = 'read-ratio' + rwdDivCount;
		rwdElements[2].name = 'write-ratio'+ rwdDivCount;
		rwdElements[3].name = 'delete-ratio' + rwdDivCount;
		rwdDivCount++;
	}
	
	function setWorkloadNumberElement (workloadNumberElement) {		
		workloadNumberElement.value = workloadDivCount;
		workloadDivCount++;
	}
</script>
</body>
</html>  