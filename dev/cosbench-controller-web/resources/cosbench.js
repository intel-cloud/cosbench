function checkAll(str) {
	var a = document.getElementsByName(str);
	for ( var i = 0; i < a.length; i++)
		a[i].checked = window.event.srcElement.checked;
}

function checkItem(allname) {
	var e = window.event.srcElement;
	var all = eval("document.all." + allname);
	check(e, all);
}

function checkMe(id, allname) {
	document.getElementById('checkbox-' + id).checked = !document
			.getElementById('checkbox-' + id).checked;
	var all = eval("document.all." + allname);
	check(document.getElementById('checkbox-' + id), all);
}

function check(item, all) {
	if (item.checked) {
		var a = document.getElementsByName(item.name);
		all.checked = true;
		for ( var i = 0; i < a.length; i++) {
			if (!a[i].checked) {
				all.checked = false;
				break;
			}
		}
	} else
		all.checked = false;
}

function findChecked(name) {
	var a = document.getElementsByName(name);
	var value = "";
	for ( var i = 0; i < a.length; i++) {
		if (a[i].checked) {
			value += a[i].value;
			value += "_";
		}
	}
	return value.substring(0, value.length - 1);
}

function changePriority(id, value) {
	var neighid = findChecked('ActiveWorkload');
	if (neighid.indexOf("_") != -1) {
		neighid = 0;
	}
	document.getElementById('neighid-' + id).value = neighid;
	document.getElementById('up-' + id).value = value;
}

function cancelWorkloads() {
	var answer = confirm("Are you sure to cancel checked workload?");
	if (answer == true) {
		var ids = findChecked('ActiveWorkload');
		document.getElementById('cancelIds').value = ids;
		document.getElementById('cancelForm').submit();
	}
}

function resubmitWorkloads() {
	var ids = findChecked('HistoryWorkload');
	document.getElementById('resubmitIds').value = ids;
	document.getElementById('resubmitForm').submit();
}
