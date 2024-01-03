(function () {
  var points = raw.models.points();

  points.dimensions().remove("size");
  //points.dimensions().remove('label');
  points.dimensions().remove("color");

  var chart = raw
    .chart()
    .title("等高线图")
    .description(
      "它显示了估计的点云密度，这对于避免在大型数据集中过度绘图特别有用。>"
    )
    .thumbnail("images/contourplot.png")
    .category("离散型")
    .model(points);

  var width = chart.number().title("宽度").defaultValue(1200).fitToWidth(true);

  var height = chart.number().title("高度").defaultValue(500);

  //left margin
  var marginLeft = chart.number().title("左边距").defaultValue(40);

  var bandwidth = chart.number().title("标准偏差 ").defaultValue(40);

  var colorMode = chart
    .list()
    .title("应用于颜色")
    .values(["Stroke", "Fill"])
    .defaultValue("Stroke");

  var useZero = chart
    .checkbox()
    .title("将原点设置为（0,0）")
    .defaultValue(false);

  var colors = chart.color().title("色标");

  var showPoints = chart.checkbox().title("显示点").defaultValue(true);

  chart.draw(function (selection, data) {
    // Retrieving dimensions from model
    var x = points.dimensions().get("x"),
      y = points.dimensions().get("y");

    var g = selection
      .attr("width", +width())
      .attr("height", +height())
      .append("g");

    //define margins
    var margin = {
      top: 0,
      right: 0,
      bottom: 20,
      left: marginLeft(),
    };

    var w = width() - margin.left,
      h = height() - margin.bottom;

    var xExtent = !useZero()
        ? d3.extent(data, function (d) {
            return d.x;
          })
        : [
            0,
            d3.max(data, function (d) {
              return d.x;
            }),
          ],
      yExtent = !useZero()
        ? d3.extent(data, function (d) {
            return d.y;
          })
        : [
            0,
            d3.max(data, function (d) {
              return d.y;
            }),
          ];

    var xScale =
      x.type() == "Date"
        ? d3.scaleTime().range([margin.left, width()]).domain(xExtent)
        : d3.scaleLinear().range([margin.left, width()]).domain(xExtent);

    var yScale =
      y.type() == "Date"
        ? d3.scaleTime().range([h, 0]).domain(yExtent)
        : d3.scaleLinear().range([h, 0]).domain(yExtent);

    var xAxis = d3.axisBottom(xScale).tickSize(6, -h);
    var yAxis = d3.axisLeft(yScale).ticks(10).tickSize(6, -w);

    g.append("clipPath")
      .attr("id", "clip")
      .append("rect")
      .attr("class", "mesh")
      .attr("width", w)
      .attr("height", h)
      .attr("transform", "translate(" + margin.left + ",1)");

    var contours = d3
      .contourDensity()
      .x(function (d) {
        return xScale(d.x);
      })
      .y(function (d) {
        return yScale(d.y);
      })
      .size([w, h])
      .bandwidth(bandwidth())(data);

    colors.domain(contours, function (d) {
      return d.value;
    });

    var contourPaths = g
      .insert("g", "g")
      .attr("clip-path", "url(#clip)")
      .attr("stroke-linejoin", "round")
      .selectAll("path")
      .data(contours)
      .enter()
      .append("path")
      .attr("d", d3.geoPath());

    if (colorMode() == "Fill") {
      contourPaths
        .attr("fill", function (d) {
          return colors()(d.value);
        })
        .attr("stroke", "none");
    } else if (colorMode() == "Stroke") {
      contourPaths
        .attr("stroke", function (d) {
          return colors()(d.value);
        })
        .attr("fill", "none");
    }

    var point = g
      .selectAll("g.point")
      .data(data)
      .enter()
      .append("g")
      .attr("class", "point");

    point
      .append("circle")
      .filter(function () {
        return showPoints();
      })
      .style("fill", "#000")
      .attr("transform", function (d) {
        return "translate(" + xScale(d.x) + "," + yScale(d.y) + ")";
      })
      .attr("r", 1);

    var labels = g
      .selectAll("g.labels")
      .data(data)
      .enter()
      .append("g")
      .attr("class", "labels");

    labels
      .append("text")
      .attr("transform", (d) => {
        return `translate(${xScale(d.x)}, ${yScale(d.y)})`;
      })
      .attr("text-anchor", "middle")
      .style("font-size", "10px")
      .attr("dy", 15)
      .style("font-family", "Arial, Helvetica")
      .text((d) => {
        return d.label ? d.label.join(", ") : "";
      });

    g.append("g")
      .attr("class", "y axis")
      .attr("transform", "translate(" + margin.left + ",0)")
      .call(yAxis);

    g.append("g")
      .attr("class", "x axis")
      .attr("transform", "translate(0," + h + ")")
      .call(xAxis);

    g.selectAll(".axis")
      .selectAll("text")
      .style("font", "10px Arial, Helvetica");

    g.selectAll(".axis")
      .selectAll("path")
      .style("fill", "none")
      .style("stroke", "#000000")
      .style("shape-rendering", "crispEdges");

    g.selectAll(".axis")
      .selectAll("line")
      .style("fill", "none")
      .style("stroke", "#000000")
      .style("shape-rendering", "crispEdges");
  });
})();
