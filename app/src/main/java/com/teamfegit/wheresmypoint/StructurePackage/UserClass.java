package com.teamfegit.wheresmypoint.StructurePackage;

/**
 * Created by Eustace on 17-Oct-16.
 */

public class UserClass {

    public String nuid;
    public String phoneno;
    public String pointno;
    public String homelocation;
    public String currentlocation;

    public double l_distance;
    public double l_time;


    public UserClass(String nuid, String phoneno, String pointno, String homelocation, String currentlocation) {
        this.nuid = nuid;
        this.phoneno = phoneno;
        this.pointno = pointno;
        this.homelocation = homelocation;
        this.currentlocation = currentlocation;
    }
}
