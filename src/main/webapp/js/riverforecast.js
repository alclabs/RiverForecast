$(document).ready(function(event) {
    $.ajaxSetup({ cache: false });
    var $riverSelect = $("#riverName");
    var $stationSelect = $("#stationName");
    var $valueTypeSelect = $("#valueType");
    var $mbPath = $("#mbPath");
    var $addButton = $("#addButton");
    var $deleteButton = $("#deleteButton");
    var $mappingTable = $("#mappings");
debugger;
    $riverSelect.change(function() {
        var riverName = getRiverSelection();
        if(riverName !== "") {
            initStationOptions(riverName);
        }
    });
    $addButton.click(function () {
        var riverName = getRiverSelection();
        var stationName = getStationSelection();
        var valueType = getValueTypeSelection();
        var mbPath = getMbPath();
        addMapping(riverName, stationName, valueType, mbPath);
    });

    function getRiverSelection() {
        return $riverSelect.val();
    }
    function getStationSelection() {
        return $stationSelect.val();
    }
    function getValueTypeSelection() {
        return $valueTypeSelect.val();
    }
    function getMbPath() {
        return $mbPath.val();
    }
    
    function initRiverOptions() {
        $riverSelect.empty();
        addInstructionOption($riverSelect, "Please select a river");
        $.getJSON("config/getRivers"
        ).done(function(data, status, xhr) {
            addOptions($riverSelect, data);
        }).fail(function(xhr, status, error) {
            alert("Error getting list of rivers.");
        });
    }
    function initStationOptions(riverName) {
        $stationSelect.empty();
        addInstructionOption($stationSelect, "Please select a station");
        if(riverName) {
            $.getJSON("config/getStations", {
                'riverName': riverName
            }).done(function (data, status, xhr) {
                addOptions($stationSelect, data);
            }).fail(function (xhr, status, error) {
                alert("Error retrieving list of station names.");
            });
        }
    }
    function initValueTypes() {
        $valueTypeSelect.empty();
        addInstructionOption($valueTypeSelect, "Please select a value type");
        $.getJSON("config/getValueTypes"
        ).done(function(data, status, xhr) {
            addOptions($valueTypeSelect, data);
        }).fail(function(xhr, status, error) {
            alert("Error getting list of value types.");
        });
    }
    function addInstructionOption($selectbox, text) {
        $selectbox.append($('<option>', {
            value: "",
            text: text,
            disabled: true,
            selected: true
        }));
    }
    function addOptions($selectbox, options) {
        $.each(options, function(i, option) {
           $selectbox.append($('<option>', {
               value: option,
               text: option
           }));
        });
    }

    $mappingTable.DataTable({
        "language" : {
            "zeroRecords" : "There are no mappings to show."
        },
        "columns" : [
            { "data" : "riverName" },
            { "data" : "stationName" },
            { "data" : "valueType" },
            { "data" : "mbPath" }
        ],
        "columnDefs" : [
            { "targets" : [0],
              "visible" : true },
            { "targets" : [1],
              "visible" : true },
            { "targets" : [2],
              "visible" : true },
            { "targets" : [3],
              "visible" : true }
        ]
    });
    var mappingTable = $mappingTable.DataTable();

    $mappingTable.on('click', 'tr', function(){
        if($(this).hasClass('selected')) {
            $(this).removeClass('selected');
        } else {
            mappingTable.$('tr.selected').removeClass('selected');
            $(this).addClass('selected');
        }
    });

    $deleteButton.click(function() {
        var selectedRow = mappingTable.row('.selected');
        var rowdata = selectedRow.data();
        removeMapping(rowdata, function() {
            selectedRow.remove().draw(false);
        });
    });

    function initMappingTable() {
        $.getJSON("config/getMappings"
        ).done(function(data, status, xhr) {
            loadMappingTable(data);
        })
        .fail(function(xhr, status, error) {
            alert("Error retrieving mappings.");
        })
    }

    function loadMappingTable(data) {
        mappingTable.clear();
        $.each(data, function(index, mapping) {
            mappingTable.row.add(mapping);
        });
        mappingTable.draw();
    }

    function addMapping(riverName, stationName, valueType, mbPath) {
        if(riverName && stationName && mbPath) {
            $.post("config/addMapping", {
                'riverName' : riverName,
                'stationName' : stationName,
                'valueType' : valueType,
                'mbPath' : mbPath
            }).done(function(data) {
                if(data.success === true) {
                    mappingTable.row.add({
                        "riverName" : riverName,
                        "stationName" : stationName,
                        "valueType" : valueType,
                        "mbPath" : mbPath
                    }).draw(false);
                } else {
                    alert("Error adding mapping: " + data.message);
                }
            }).fail(function(xhr, status, error) {
                alert("Error adding mapping.");
            });
        } else {
            alert("River, Station, or microblock has not been specified.");
        }
    }
    function removeMapping(rowdata, updateTable) {
        console.log(rowdata);
        if(rowdata) {
            $.post("config/removeMapping", {
                'riverName' : rowdata.riverName,
                'stationName' : rowdata.stationName,
                'valueType' : rowdata.valueType,
                'mbPath' : rowdata.mbPath
            }).done(function(data) {
                if(data.success === true) {
                    updateTable();
                } else {
                    alert("Error deleting mapping: " + data.message);
                }
            }).fail(function(xhr, status, error) {
                alert("Error deleting mapping.");
            });
        }
    }

    initRiverOptions();
    initStationOptions();
    initValueTypes();
    initMappingTable();
});