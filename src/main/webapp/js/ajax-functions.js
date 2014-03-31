function doSearch(dataArr, successFunc) {
    log('getProjectsAjax function running');

    $.ajax({
        type: "POST",
        dataType: "json",
        url: ajaxSearchUrl,
        cache: false,
        data: dataArr,
        success: function(data, success) {
            console.log("success");
            console.log("success", arguments);
            console.log("data", typeof data, data); // Verify the response

            /*data = {
                    no_projects: false, page_type: "in_process", projects: {1: {
                    project_id: "12345",
                    project_name: "my project",
                    description: "This is a random text!",
                    user: "Allard van Altena",
                    date_started: "2014-02-26 12:55:08.0",
                    latest_status: "1 On Hold",
                    inputs: {
                        1: {
                            "name": "receptor_file.pdbqt",
                            "scan_id": null,
                            "subject": null,
                            "type": null,
                            "format": ".pdbqt"
                        },
                        2: {
                            "name": "config_file.txt",
                            "scan_id": null,
                            "subject": null,
                            "type": null,
                            "format": ".txt"
                        },
                        3: {
                            "name": "ligands_zip.zip",
                            "scan_id": "1234",
                            "subject": null,
                            "type": null,
                            "format": ".txt"
                        },
                    },
                    outputs: {
                        1: {
                            "name": "receptor_file.pdbqt",
                            "scan_id": null,
                            "subject": null,
                            "type": null,
                            "format": ".pdbqt"
                        }
                    },
                    provenance_count: 0
                }, 2: {
                    project_id: "1234",
                    project_name: "my project",
                    description: "This is a random text!",
                    user: "Allard van Altena",
                    date_started: "2014-02-26 12:55:08.0",
                    latest_status: "1 On Hold",
                    inputs: {
                        1: {
                            "name": "receptor_file.pdbqt",
                            "scan_id": null,
                            "subject": null,
                            "type": null,
                            "format": ".pdbqt"
                        },
                        2: {
                            "name": "config_file.txt",
                            "scan_id": null,
                            "subject": null,
                            "type": null,
                            "format": ".txt"
                        },
                        3: {
                            "name": "ligands_zip.zip",
                            "scan_id": "1234",
                            "subject": null,
                            "type": null,
                            "format": ".txt"
                        },
                    },
                    outputs: {
                        1: {
                            "name": "receptor_file.pdbqt",
                            "scan_id": null,
                            "subject": null,
                            "type": null,
                            "format": ".pdbqt"
                        }
                    },
                    provenance_count: 0
                }, 3: {
                    project_id: "123",
                    project_name: "my project",
                    description: "This is a random text!",
                    user: "Allard van Altena",
                    date_started: "2014-02-26 12:55:08.0",
                    latest_status: "1 On Hold",
                    inputs: {
                        1: {
                            "name": "receptor_file.pdbqt",
                            "scan_id": null,
                            "subject": null,
                            "type": null,
                            "format": ".pdbqt"
                        },
                        2: {
                            "name": "config_file.txt",
                            "scan_id": null,
                            "subject": null,
                            "type": null,
                            "format": ".txt"
                        },
                        3: {
                            "name": "ligands_zip.zip",
                            "scan_id": "1234",
                            "subject": null,
                            "type": null,
                            "format": ".txt"
                        },
                    },
                    outputs: {
                        1: {
                            "name": "receptor_file.pdbqt",
                            "scan_id": null,
                            "subject": null,
                            "type": null,
                            "format": ".pdbqt"
                        }
                    },
                    provenance_count: 0
                }}};*/

            successFunc(data);
        }
    });
}

function getLigands(successFunc) {
    log('getLigands function running');

    $.ajax({
        type: "GET",
        dataType: "json",
        url: ajaxLigandsUrl,
        cache: false,
        success: function(data, success) {
            console.log("success");
            console.log("success", arguments);
            console.log("data", typeof data, data); // Verify the response

            successFunc(data);
        }
    });
}

function updateStatus(dataArr, successFunc) {
    log('updateStatus function running');

    $.ajax({
        type: "GET",
        dataType: "json",
        url: ajaxUpdateStatusUrl,
        cache: false,
        data: dataArr,
        success: function(data, success) {
            console.log("success");
            console.log("success", arguments);
            console.log("data", typeof data, data); // Verify the response

            successFunc(data);
        }
    });
}