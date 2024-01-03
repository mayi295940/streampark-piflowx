(function () {
  var points = raw.models.points();

  points.dimensions().remove("size");
  points.dimensions().remove("label");
  points.dimensions().remove("color");

  var chart = raw
    .chart()
    .title("六边形分箱图")
    .description(
      " 将人口最多的地区在散点图上进行视觉上的聚类。当绘制数百个点时，可以使散点图更具可读性。"
    )
    .thumbnail("images/binning.png")
    .category("离散型")
    .model(points);

  var width = chart.number().title("宽度").defaultValue(1200).fitToWidth(true);

  var height = chart.number().title("高度").defaultValue(500);

  //left margin
  var marginLeft = chart.number().title("左边距").defaultValue(40);

  var radius = chart.number().title("半径").defaultValue(20);

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

    var hexbin = d3
      .hexbin()
      .size([w, h])
      .x(function (d) {
        return xScale(d.x);
      })
      .y(function (d) {
        return yScale(d.y);
      })
      .radius(+radius());

    g.append("clipPath")
      .attr("id", "clip")
      .append("rect")
      .attr("class", "mesh")
      .attr("width", w)
      .attr("height", h)
      .attr("transform", "translate(" + margin.left + ",1)");

    colors.domain(hexbin(data), function (d) {
      return d.length;
    });

    g.append("g")
      .attr("clip-path", "url(#clip)")
      .selectAll(".hexagon")
      .data(hexbin(data))
      .enter()
      .append("path")
      .attr("class", "hexagon")
      .attr("d", hexbin.hexagon())
      .attr("transform", function (d) {
        return "translate(" + d.x + "," + d.y + ")";
      })
      .style("fill", function (d) {
        return colors()(d.length);
      })
      .attr("stroke", "#000")
      .attr("stroke-width", ".5px");

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
