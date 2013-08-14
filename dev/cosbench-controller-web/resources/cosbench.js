function checkAll(event, str) {
	var e = window.event ? window.event.srcElement : event.currentTarget;
	var a = document.getElementsByName(str);
	for ( var i = 0; i < a.length; i++)
		a[i].checked = e.checked;
}

function checkItem(event, allId) {
	func = checkItem.caller;
	var e = window.event ? window.event.srcElement : event.currentTarget;
	var all = document.getElementById(allId);
	check(e, all);
}

function checkMe(id, allId) {
	document.getElementById('checkbox-' + id).checked = !document
			.getElementById('checkbox-' + id).checked;
	var all = document.getElementById(allId);
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
