package vn.viettuts.qlsv.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


import vn.viettuts.qlsv.view.HotelBooking;

public class StudentController implements ActionListener{
   
    private HotelBooking hotelBooking;

    public StudentController(HotelBooking hotelBooking) {
        this.hotelBooking=hotelBooking;
       

    }

    public void actionPerformed(ActionEvent e) {
       hotelBooking.setVisible(true);
    }
}
