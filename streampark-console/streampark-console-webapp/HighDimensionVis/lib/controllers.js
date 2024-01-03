"use strict";

/* Controllers */
angular
  .module("raw.controllers", [])
  .controller("RawCtrl", function ($scope, dataService, $http, $timeout, $sce) {
    let tableli = document.querySelector(".list-type");
    tableli.children[1].style.display = "none";
    tableli.children[0].style.display = "none";
    $scope.loading = false;

    // Clipboard
    $scope.$watch("clipboardText", (text) => {
      if (!text) return;

      $scope.loading = true;

      if (is.url(text)) {
        $scope.importMode = "url";
        $timeout(function () {
          $scope.url = text;
        });
        return;
      }

      try {
        var json = JSON.parse(text);
        selectArray(json);
        $scope.loading = false;
      } catch (error) {
        parseText(text);
      }
    });

    $scope.antani = (d) => {
      $scope.loading = true;
      var json = dataService.flatJSON(d);
      parseText(d3.tsvFormat(json));
    };

    // select Array in JSON
    function selectArray(json) {
      $scope.json = json;
      $scope.structure = [];
      expand(json);
    }

    // parse Text
    function parseText(text) {
      //  $scope.loading = false;
      $scope.json = null;
      $scope.text = text;
      $scope.parse(text);
    }

    // load File
    $scope.uploadFile = (file) => {
      if (file.size) {
        $scope.loading = true;

        // excel
        if (
          file.name.search(/\.xls|\.xlsx/) != -1 ||
          file.type.search("sheet") != -1
        ) {
          dataService.loadExcel(file).then((worksheets) => {
            $scope.fileName = file.name;
            $scope.loading = false;
            // multiple sheets
            if (worksheets.length > 1) {
              $scope.worksheets = worksheets;
              // single > parse
            } else {
              $scope.parse(worksheets[0].text);
            }
          });
        }

        // json
        if (file.type.search("json") != -1) {
          dataService.loadJson(file).then((json) => {
            $scope.fileName = file.name;
            selectArray(json);
          });
        }

        // txt
        if (file.type.search("text") != -1) {
          dataService.loadText(file).then((text) => {
            $scope.fileName = file.name;
            parseText(text);
          });
        }
      }
    };

    function parseData(json) {
      $scope.loading = false;
      //  $scope.parsed = true;

      if (!json) return;
      try {
        selectArray(json);
      } catch (error) {
        console.log(error);
        parseText(json);
      }
    }

    // load URl
    $scope.$watch("url", (url) => {
      if (!url || !url.length) {
        return;
      }

      /*if (is.not.url(url)) {
        $scope.error = "请输入一个有效的URL";
        return;
      }*/

      $scope.loading = true;
      var error = null;
      // first trying jsonp
      $http
        .jsonp($sce.trustAsResourceUrl(url), {jsonpCallbackParam: "callback"})
        .then(
          (response) => {
            $scope.fileName = url;
            parseData(response.data);
          },
          (response) => {
            $http
              .get($sce.trustAsResourceUrl(url), {
                responseType: "arraybuffer",
              })
              .then(
                (response) => {
                  var data = new Uint8Array(response.data);
                  var arr = new Array();
                  for (var i = 0; i != data.length; ++i)
                    arr[i] = String.fromCharCode(data[i]);
                  var bstr = arr.join("");

                  try {
                    var workbook = XLS.read(bstr, {type: "binary"});
                    var worksheets = [];
                    var sheet_name_list = workbook.SheetNames;

                    sheet_name_list.forEach(function (y) {
                      var worksheet = workbook.Sheets[y];
                      worksheets.push({
                        name: y,
                        text: XLSX.utils.sheet_to_csv(worksheet),
                        rows: worksheet["!range"].e.r,
                      });
                    });

                    $scope.fileName = url;
                    $scope.loading = false;

                    // multiple sheets
                    if (worksheets.length > 1) {
                      $scope.worksheets = worksheets;
                      // single > parse
                    } else {
                      parseText(worksheets[0].text);
                    }
                  } catch (error) {
                    $scope.fileName = url;
                    try {
                      var json = JSON.parse(bstr);
                      selectArray(json);
                    } catch (error) {
                      parseText(bstr);
                    }
                  }
                },
                (response) => {
                  $scope.loading = false;
                  $scope.error = "您提供的URL有问题。请确认地址是否正确。";
                }
              );
          }
        );
    });

    //

    $scope.samples = [
      {
        title: "Biggest cities per continent",
        type: "分布型",
        url: "data/cities.csv",
      },
      {title: "Countries GDP", type: "其他型", url: "data/countriesGDP.csv"},
      {title: "Cars", type: "多变量型", url: "data/multivariate.csv"},
      {title: "Movies", type: "分散型", url: "data/dispersions.csv"},
      {title: "Music industry", type: "时间序列型", url: "data/music.csv"},
      {title: "Lineup", type: "时间块型", url: "data/lineup.tsv"},
      {
        title: "Orchestras",
        type: "层次结构(加权)型",
        url: "data/orchestra.csv",
      },
      {title: "Animal kingdom", type: "层次结构型", url: "data/animals.tsv"},
      {
        title: "Titanic's passengers",
        type: "多分类型",
        url: "data/titanic.tsv",
      },
      {
        title: "Most frequent letters",
        type: "矩阵(窄)型",
        url: "data/letters.tsv",
      },
    ];
    // form-control ng-pristine ng-valid ng-scope ng-not-empty ng-valid-min ng-touched
    $scope.selectSample = (sample) => {
      if (!sample) return;
      $scope.text = "";
      $scope.loading = true;
      dataService.loadSample(sample.url).then(
        (data) => {
          $scope.text = data.replace(/\r/g, "");
          $scope.loading = false;
        },
        (error) => {
          $scope.error = error;
          $scope.loading = false;
        }
      );
      $('#li-four').addClass('on');
      $('body').css('overflow-y', 'auto');
    };
    $(document.getElementById("load-data")).on("dragenter", function (e) {
      $scope.importMode = "file";
      // $("#li-four a").css();
      $scope.parsed = false;
      // $scope.$digestAsync();
      // $scope.$applyAsync();
    });
    $scope.$watch("dataView", function (n, o) {
      if (!$(".parsed .CodeMirror")[0]) return;

      var cm = $(".parsed .CodeMirror")[0].CodeMirror;
      $timeout(function () {
        cm.refresh();
      });
    });
    // init
    $scope.raw = raw;
    $scope.data = [];
    $scope.metadata = [];
    $scope.error = false;
    //  $scope.loading = true;

    $scope.importMode = "clipboard";

    $scope.categories = [
      "Hierarchies",
      "Time Series",
      "Distributions",
      "Correlations",
      "Others",
    ];

    $scope.bgColors = {
      Hierarchies: "#0f0",
      "Time Series": "rgb(255, 185, 5)",
      Distributions: "rgb(5, 205, 255)",
      Correlations: "#df0",
      Others: "#0f0",
    };

    $scope.$watch("files", function () {
      $scope.uploadFile($scope.files);
    });

    $scope.log = "";
    $scope.files = [];

    let initUrl
    let url
    initUrl = window.location.href
    // initUrl = initUrl + '?url=aHR0cDovL2xvY2FsaG9zdDo2MzM0Mi9IaWdoRGltZW5zaW9uVmlzL2RhdGEvY291bnRyaWVzR0RQLmNzdg=='


    let urlParams = initUrl.split('?')[1]
    let obj = {}
    if (urlParams) {
      let arr = urlParams.split('&')
      for (let i = 0; i < arr.length; i++) {
        let params = arr[i].split('=')
        obj[params[0]] = params[1]
      }
      url = decodeURIComponent(escape(window.atob(decodeURIComponent(obj.url))))
    }
    // console.log('obj.url为:', url)
    // console.log(window.btoa(unescape(encodeURIComponent('http://localhost:63342/HighDimensionVis/data/countriesGDP.csv'))))
    // console.log('url为:', Base64.encode('http://localhost:63342/HighDimensionVis/data/cities.csv'))
    $scope.$watch("importMode", function () {
      let that = this;
      // reset
      $scope.parsed = false;
      $scope.loading = false;
      $scope.clipboardText = "";
      $scope.unstacked = false;
      $scope.text = "";
      $scope.data = [];
      $scope.json = null;
      $scope.worksheets = [];
      $scope.fileName = null;
      $scope.url = url;
      // $scope.url = '';
      // console.log($scope);
    });

    var arrays = [];

    $scope.unstack = function () {
      if (!$scope.stackDimension) return;
      var data = $scope.data;
      var base = $scope.stackDimension.key;

      var unstacked = [];

      data.forEach((row) => {
        for (var column in row) {
          if (column == base) continue;
          var obj = {};
          obj[base] = row[base];
          obj.column = column;
          obj.value = row[column];
          unstacked.push(obj);
        }
      });
      $scope.oldData = data;
      parseText(d3.tsvFormat(unstacked));

      $scope.unstacked = true;
    };

    $scope.stack = function () {
      parseText(d3.tsvFormat($scope.oldData));
      $scope.unstacked = false;
    };

    function jsonTree(json) {
      // mettere try
      var tree = JSON.parse(json);
      $scope.json = tree;
      $scope.structure = [];
      //console.log(JSON.parse(json));
      expand(tree);
    }

    function expand(parent) {
      for (var child in parent) {
        if (is.object(parent[child]) || is.array(parent[child])) {
          expand(parent[child]);
          if (is.array(parent[child])) arrays.push(child);
        }
      }
      //console.log(child,parent[child])
    }

    // very improbable function to determine if pivot table or not.
    // pivotable index
    // calculate if values repeat themselves
    // calculate if values usually appear in more columns

    function pivotable(array) {
      var n = array.length;
      var rows = {};

      array.forEach((o) => {
        for (var p in o) {
          if (!rows.hasOwnProperty(p)) rows[p] = {};
          if (!rows[p].hasOwnProperty(o[p])) rows[p][o[p]] = -1;
          rows[p][o[p]] += 1;
        }
      });

      for (var r in rows) {
        for (var p in rows[r]) {
          for (var ra in rows) {
            if (r == ra) break;
            //    if (p == "") break;
            if (rows[ra].hasOwnProperty(p)) rows[r][p] -= 2.5;
          }
        }
      }

      var m = d3
        .values(rows)
        .map(d3.values)
        .map((d) => {
          return d3.sum(d) / n;
        });
      //console.log(d3.mean(m),m)
      $scope.pivot = d3.mean(m);
    }

    $scope.parse = (text) => {
      if ($scope.model) $scope.model.clear();

      $scope.text = text;
      $scope.data = [];
      $scope.metadata = [];
      $scope.error = false;
      // $scope.importMode = null;
      //$scope.$apply();
      // $scope.$applyAsync();

      if (!text) return;

      try {
        var parser = raw.parser();
        $scope.data = parser(text);
        $scope.metadata = parser.metadata(text);
        $scope.error = false;
        pivotable($scope.data);
        $scope.parsed = true;

        $timeout(function () {
          $scope.charts = raw.charts.values().sort(function (a, b) {
            return (
              d3.ascending(a.category(), b.category()) ||
              d3.ascending(a.title(), b.title())
            );
          });
          $scope.chart = $scope.charts.filter((d) => {
            return d.title() == "散点图";
          })[0];
          $scope.model = $scope.chart ? $scope.chart.model() : null;
        });
      } catch (e) {
        $scope.data = [];
        $scope.metadata = [];
        $scope.error = e.name == "ParseError" ? +e.message : false;
      }
      if (!$scope.data.length && $scope.model) $scope.model.clear();
      $scope.loading = false;
      var cm = $(".parsed .CodeMirror")[0].CodeMirror;
      $timeout(function () {
        cm.refresh();
        cm.refresh();
      });
    };

    $scope.delayParse = dataService.debounce($scope.parse, 500, false);

    $scope.$watch("text", (text) => {
      if (!text) return;
      $scope.loading = true;
      $scope.delayParse(text);
    });

    $scope.$watch("error", (error) => {
      if (!$(".parsed .CodeMirror")[0]) return;
      var cm = $(".parsed .CodeMirror")[0].CodeMirror;
      if (!error) {
        cm.removeLineClass($scope.lastError, "wrap", "line-error");
        return;
      }
      cm.addLineClass(error, "wrap", "line-error");
      cm.scrollIntoView(error);
      $scope.lastError = error;
    });

    $("body").mousedown(function (e, ui) {
      if ($(e.target).hasClass("dimension-info-toggle")) return;
      $(".dimensions-wrapper").each((e) => {
        angular.element(this).scope().open = false;
        angular.element(this).scope().$apply();
      });
    });

    $scope.codeMirrorOptions = {
      dragDrop: false,
      lineNumbers: true,
      lineWrapping: true,
    };

    $scope.selectChart = (chart) => {
      if (chart == $scope.chart) return;
      $scope.model.clear();
      $scope.chart = chart;
      $scope.model = $scope.chart.model();
    };

    function refreshScroll() {
      $('[data-spy="scroll"]').each(function () {
        $(this).scrollspy("refresh");
      });
    }

    $(window).scroll(function () {
      // check for mobile
      if ($(window).width() < 760 || $("#mapping").height() < 300) return;

      var scrollTop = $(window).scrollTop() + 0,
        mappingTop = $("#mapping").offset().top + 10,
        mappingHeight = $("#mapping").height(),
        isBetween =
          scrollTop > mappingTop + 50 &&
          scrollTop <= mappingTop + mappingHeight - $(".sticky").height() - 20,
        isOver =
          scrollTop > mappingTop + mappingHeight - $(".sticky").height() - 20,
        mappingWidth = mappingWidth ? mappingWidth : $(".mapping").width();

      if (mappingHeight - $(".dimensions-list").height() > 90) return;
      //console.log(mappingHeight-$('.dimensions-list').height())
      if (isBetween) {
        $(".sticky")
          .css("position", "fixed")
          .css("width", mappingWidth + "px")
          .css("top", "20px");
      }

      if (isOver) {
        $(".sticky")
          .css("position", "fixed")
          .css("width", mappingWidth + "px")
          .css(
            "top",
            mappingHeight -
            $(".sticky").height() +
            0 -
            scrollTop +
            mappingTop +
            "px"
          );
        return;
      }

      if (isBetween) return;

      $(".sticky").css("position", "relative").css("top", "").css("width", "");
    });

    $scope.sortCategory = (chart) => {
      // sort first by category, then by title
      return [chart.category(), chart.title()];
    };

    $(document).ready(refreshScroll);
    //隐藏自定义图表
    let layout = document.querySelector(".layout");
    layout.style.display = "none";
  });
