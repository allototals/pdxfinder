package org.pdxfinder.preload;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


import java.util.ArrayList;
import java.util.List;

public class CustomXlsxReaderTests {

    private Workbook workbook;
    private Sheet sheet;
    private String expectedString = "9999";
    private int expectedInteger = 9999;
    private Row secondDataRow;
    private Row thirdDataRow;
    private String ASpace = " ";
    private String moreSpaces = "    ";
    private String blank = "";

    PDX_XlsxReader xlsxReader = new PDX_XlsxReader();

    @Before
    public void init(){

        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("TEST");
        sheet.createRow(0);
        secondDataRow = sheet.createRow(1);
        thirdDataRow = sheet.createRow(2);
    }

    @Test
    public void Given_StringsAndInt_When_IterateThroughSheetIsCalled_Then_returnAllSheetsOfData(){

        //given init()
        fillRowWithCharacters(secondDataRow, expectedString);
        fillRowWithIntegers(thirdDataRow, expectedInteger);

        //When
        ArrayList<ArrayList<String>> actualList = xlsxReader.iterateThroughSheet(sheet);

        //Then
        Assert.assertEquals(3,actualList.size());

        List<String> actualSecondRow = actualList.get(1);
        List<String> actualThirdRow = actualList.get(2);

        AssertEachElementEqualsString(actualSecondRow, expectedString);

        actualThirdRow.forEach(c ->
                Assert.assertEquals(expectedInteger, Integer.parseInt(c)));
    }

    @Test
    public void Given_WithEmptySpaces_When_IterateThroughSheetIsCalled_Then_returnCleanToBlankStrings(){

        //given init()

        for(int i = 0; i < 13; i++){
            Cell newCell = secondDataRow.createCell(i);
            newCell.setCellValue(" ");
        }

        //When
        ArrayList<ArrayList<String>> actualList = xlsxReader.iterateThroughSheet(sheet);

        //Then
        AssertSizeAndEachStringIsBlank(actualList);
    }

    @Test
    public void Given_WithMultipleEmptySpaces_When_IterateThroughSheetIsCalled_Then_returnCleanToBlankStrings(){

        //given init()
        for(int i = 0; i < 13; i++){
            Cell newCell = secondDataRow.createCell(i);
            newCell.setCellValue(moreSpaces);
        }

        //When
        ArrayList<ArrayList<String>> actualList = xlsxReader.iterateThroughSheet(sheet);

        //Then
        AssertSizeAndEachStringIsBlank(actualList);
    }

    @Test
    public void Given_blankCells_When_IterateThroughSheetIsCalled_Then_returnCorrectCellCount(){

        //given init()
        int expectedSize =24;
        String empty = "";

        for(int i = 0; i < expectedSize+1; i++){
            Cell newCell = secondDataRow.createCell(i);
            newCell.setCellValue("");
        }

        //When
        ArrayList<ArrayList<String>> actualList = xlsxReader.iterateThroughSheet(sheet);

        //Then
        Assert.assertEquals(expectedSize, actualList.get(1).size());
    }


    private void fillRowWithCharacters(Row row, String fill){

        for(int i = 0; i < 13; i++){
            Cell newCell = row.createCell(i);
            newCell.setCellValue(fill);
        }
    }

    private void fillRowWithIntegers(Row row, int fill){

        for(int i = 0; i < 13; i++){
            Cell newCell = row.createCell(i);
            newCell.setCellValue(fill);
        }
    }

    private void AssertSizeAndEachStringIsBlank(ArrayList<ArrayList<String>> actualList){
        Assert.assertEquals(3,actualList.size());
        List<String> actualSecondRow = actualList.get(1);
        AssertEachElementEqualsString(actualSecondRow, blank);
    }

    private void AssertEachElementEqualsString(List<String> elements, String expected){
        elements.forEach(c ->
                Assert.assertEquals(expected,c));
    }

}

