
package org.n52.io.render;

import static java.awt.Color.BLACK;
import static java.awt.Color.LIGHT_GRAY;
import static java.awt.Color.WHITE;
import static java.awt.Font.BOLD;
import static java.awt.Font.PLAIN;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static org.jfree.chart.ChartFactory.createTimeSeriesChart;
import static org.n52.io.render.BarRenderer.BAR_CHART_TYPE;
import static org.n52.io.render.ChartRenderer.LabelConstants.COLOR;
import static org.n52.io.render.ChartRenderer.LabelConstants.FONT_DOMAIN;
import static org.n52.io.render.ChartRenderer.LabelConstants.FONT_LABEL;
import static org.n52.io.render.LineRenderer.LINE_CHART_TYPE;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.servlet.ServletOutputStream;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.RectangleInsets;
import org.joda.time.Interval;
import org.n52.io.v1.data.DesignedParameterSet;
import org.n52.io.v1.data.PhenomenonOutput;
import org.n52.io.v1.data.StyleProperties;
import org.n52.io.v1.data.TimeseriesDataCollection;
import org.n52.io.v1.data.TimeseriesMetadataOutput;
import org.n52.io.v1.data.TimeseriesOutput;

public abstract class ChartRenderer {

    private JFreeChart chart;

    private XYPlot xyPlot;
    
    private RenderingContext context;

    private String mimeType;

    private boolean drawLegend;

    private boolean showGrid;
    
    private boolean showTooltips;

    private String language;

    public ChartRenderer(RenderingContext context) {
        this.context = context;
        this.xyPlot = createChart(context);
    }

    public abstract void renderChart(TimeseriesDataCollection data);
    
    public void writeChartTo(ServletOutputStream stream) throws IOException {
        JPEGImageWriteParam p = new JPEGImageWriteParam(null);
        p.setCompressionMode(JPEGImageWriteParam.MODE_DEFAULT);
        ImageIO.write(drawChartToImage(), "png", stream);
    }

    private BufferedImage drawChartToImage() {
        int width = getChartStyleDefinitions().getWidth();
        int height = getChartStyleDefinitions().getHeight();
        BufferedImage chartImage = new BufferedImage(width, height, TYPE_INT_RGB);
        Graphics2D chartGraphics = chartImage.createGraphics();
        chartGraphics.fillRect(0, 0, width, height);
        chartGraphics.setColor(WHITE);
        chart.draw(chartGraphics, new Rectangle2D.Float(0, 0, width, height));
        return chartImage;
    }
    
    public XYPlot getXYPlot() {
        return xyPlot;
    }
    
    public RenderingContext getRenderingContext() {
        return context;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public boolean isDrawLegend() {
        return drawLegend;
    }

    public void setDrawLegend(boolean drawLegend) {
        this.drawLegend = drawLegend;
    }

    public boolean isShowGrid() {
        return showGrid;
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
    }

    public boolean isShowTooltips() {
        return showTooltips;
    }

    public void setShowTooltips(boolean showTooltips) {
        this.showTooltips = showTooltips;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    private XYPlot createChart(RenderingContext context) {
        
        // TODO add language context
        
        this.chart = createTimeSeriesChart(null, "Time", "Value", null, false, showTooltips, true);
        return createPlotArea(chart);
    }

    private XYPlot createPlotArea(JFreeChart chart) {
        XYPlot xyPlot = chart.getXYPlot();
        xyPlot.setBackgroundPaint(WHITE);
        xyPlot.setDomainGridlinePaint(LIGHT_GRAY);
        xyPlot.setRangeGridlinePaint(LIGHT_GRAY);
        xyPlot.setAxisOffset(new RectangleInsets(2.0, 2.0, 2.0, 2.0));
        showCrosshairsOnAxes(xyPlot);
        showGridlinesOnChart(xyPlot);
        configureDomainAxis(xyPlot);
        configureTimeAxis(xyPlot);
        return xyPlot;
    }

    private void configureDomainAxis(XYPlot xyPlot) {
        ValueAxis domainAxis = xyPlot.getDomainAxis();
        domainAxis.setTickLabelFont(FONT_DOMAIN);
        domainAxis.setLabelFont(FONT_LABEL);
        domainAxis.setTickLabelPaint(COLOR);
        domainAxis.setLabelPaint(COLOR);
    }
    
    private void showCrosshairsOnAxes(XYPlot xyPlot) {
        xyPlot.setDomainCrosshairVisible(true);
        xyPlot.setRangeCrosshairVisible(true);
    }

    private void showGridlinesOnChart(XYPlot xyPlot) {
        xyPlot.setDomainGridlinesVisible(showGrid);
        xyPlot.setRangeGridlinesVisible(showGrid);
    }

    private void configureTimeAxis(XYPlot xyPlot) {
        String timespan = getChartStyleDefinitions().getTimespan();
        DateAxis timeAxis = (DateAxis) xyPlot.getDomainAxis();
        timeAxis.setRange(getStartTime(timespan), getEndTime(timespan));
        timeAxis.setDateFormatOverride(new SimpleDateFormat());
    }

    public void configureRangeAxis(TimeseriesMetadataOutput timeseries, int seriesIndex) {
        ValueAxis rangeAxis = xyPlot.getRangeAxisForDataset(seriesIndex);
        rangeAxis.setLabel(createRangeLabel(timeseries));
        rangeAxis.setTickLabelFont(FONT_LABEL);
        rangeAxis.setLabelFont(FONT_LABEL);
        rangeAxis.setTickLabelPaint(COLOR);
        rangeAxis.setLabelPaint(COLOR);
    }

    private String createRangeLabel(TimeseriesMetadataOutput timeseriesMetadata) {
        TimeseriesOutput parameters = timeseriesMetadata.getParameters();
        PhenomenonOutput phenomenon = parameters.getPhenomenon();
        StringBuilder uom = new StringBuilder();
        uom.append(phenomenon.getLabel());
        String uomLabel = timeseriesMetadata.getUom();
        if (uomLabel != null && !uomLabel.isEmpty()) {
            uom.append(" (").append(uomLabel).append(")");
        }
        return uom.toString();
    }

    protected TimeseriesMetadataOutput[] getTimeseriesMetadataOutputs() {
        return context.getTimeseriesMetadatas();
    }

    protected StyleProperties getTimeseriesStyleFor(String timeseriesId) {
        return getChartStyleDefinitions().getStyleOptions(timeseriesId);
    }

    protected DesignedParameterSet getChartStyleDefinitions() {
        return context.getChartStyleDefinitions();
    }
    
    protected boolean isLineStyle(StyleProperties properties) {
        return isLineStyleDefault(properties) || LINE_CHART_TYPE.equals(properties.getType());
    }

    protected boolean isBarStyle(StyleProperties properties) {
        return !isLineStyleDefault(properties) || BAR_CHART_TYPE.equals(properties.getType());
    }

    private boolean isLineStyleDefault(StyleProperties properties) {
        return properties == null;
    }

    protected Date getStartTime(String timespan) {
        Interval interval = Interval.parse(timespan);
        return interval.getStart().toDate();
    }

    protected Date getEndTime(String timespan) {
        Interval interval = Interval.parse(timespan);
        return interval.getEnd().toDate();
    }
    

    static class LabelConstants {
        static final Color COLOR = BLACK;
        static final String ARIAL = "Arial";
        static final int FONT_SIZE = 12;
        static final int FONT_SIZE_TICK = 10;
        static final Font FONT_LABEL = new Font(ARIAL, BOLD, FONT_SIZE);
        static final Font FONT_DOMAIN = new Font(ARIAL, PLAIN, FONT_SIZE_TICK);
    }

}