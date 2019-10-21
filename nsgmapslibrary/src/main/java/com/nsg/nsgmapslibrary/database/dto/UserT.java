package com.nsg.nsgmapslibrary.database.dto;

import com.nsg.nsgmapslibrary.database.db.DatabaseColumn;

import java.util.ArrayList;


/**
 * Created by sailaja.ch on 03/09/2019
 */
public class UserT {
    private static final long serialVersionUID = -7074513959395013911L;
    private Integer slno ;
    private String SSOID;
    private String passwordTx;
    private String id;
    private String profileId;
    private String firstName;
    private String lastName;
    private String workingDepartment;
    private String blockId;
    private String district;
    private String designationId;
    private String roleId;
    private String createdBy;
    private String contactNo;
    private String emailId;
    private String tokenId;


    public static ArrayList<DatabaseColumn> MAPPING = new ArrayList<DatabaseColumn>();
    public static String TABLE_NAME="USER_T";
    static{

        MAPPING.add(new DatabaseColumn("slno", "setSlno",true,true,false,"int"));
        MAPPING.add(new DatabaseColumn("SSOID", "setSSOID",false,false,true,"text"));
        MAPPING.add(new DatabaseColumn("id", "setPasswordTx",false,false,true,"text"));
        MAPPING.add(new DatabaseColumn("passwordTx", "setPasswordTx",false,false,true,"text"));


        MAPPING.add(new DatabaseColumn("profileId", "setProfileId",false,false,true,"text"));
        MAPPING.add(new DatabaseColumn("firstName", "setFirstName",false,false,true,"text"));
        MAPPING.add(new DatabaseColumn("lastName", "setLastName",false,false,true,"text"));
        MAPPING.add(new DatabaseColumn("workingDepartment", "setWorkingDepartment",false,false,true,"text"));
        MAPPING.add(new DatabaseColumn("blockId", "setBlockId",false,false,true,"text"));
        MAPPING.add(new DatabaseColumn("district", "setDistrict",false,false,true,"text"));
        MAPPING.add(new DatabaseColumn("designationId", "setDesignationId",false,false,true,"text"));
        MAPPING.add(new DatabaseColumn("roleId", "setRoleId",false,false,true,"text"));
        MAPPING.add(new DatabaseColumn("createdBy", "setCreatedBy",false,false,true,"text"));
        MAPPING.add(new DatabaseColumn("contactNo", "setContactNo",false,false,true,"text"));
        MAPPING.add(new DatabaseColumn("emailId", "setEmailId",false,false,true,"text"));
        MAPPING.add(new DatabaseColumn("tokenId", "setTokenId",false,false,true,"text"));

    }
    public UserT(){

    }
    public UserT(String SSOID, String passwordTx){
        this.SSOID=SSOID;
        this.passwordTx=passwordTx;
    }

    public Integer getSlno() {
        return slno;
    }

    public void setSlno(Integer slno) {
        this.slno = slno;
    }

    public String getSSOID() { return SSOID; }

    public String setSSOID(String SSOID) { this.SSOID = SSOID;
        return SSOID;
    }

    public String getPasswordTx() { return passwordTx; }

    public String setPasswordTx(String passwordTx) { this.passwordTx = passwordTx;
        return passwordTx;
    }

    public String getId() {
        return id;
    }

    public String setId(String id) {
        this.id = id;
        return id;
    }

    public String getProfileId() {
        return profileId;
    }

    public String setProfileId(String profileId) {
        this.profileId = profileId;
        return profileId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String setFirstName(String firstName) {
        this.firstName = firstName;
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String setLastName(String lastName) {
        this.lastName = lastName;
        return lastName;
    }

    public String getWorkingDepartment() {
        return workingDepartment;
    }

    public String setWorkingDepartment(String workingDepartment) {
        this.workingDepartment = workingDepartment;
        return workingDepartment;
    }

    public String getBlockId() {
        return blockId;
    }

    public String setBlockId(String blockId) {
        this.blockId = blockId;
        return blockId;
    }

    public String getDistrict() {
        return district;
    }

    public String setDistrict(String district) {
        this.district = district;
        return district;
    }

    public String getDesignationId() {
        return designationId;
    }

    public String setDesignationId(String designationId) {
        this.designationId = designationId;
        return designationId;
    }

    public String getRoleId() {
        return roleId;
    }

    public String setRoleId(String roleId) {
        this.roleId = roleId;
        return roleId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        return createdBy;
    }

    public String getContactNo() {
        return contactNo;
    }

    public String setContactNo(String contactNo) {
        this.contactNo = contactNo;
        return contactNo;
    }

    public String getEmailId() {
        return emailId;
    }

    public String setEmailId(String emailId) {
        this.emailId = emailId;
        return emailId;
    }

    public String getTokenId() {
        return tokenId;
    }

    public String setTokenId(String tokenId) {
        this.tokenId = tokenId;
        return tokenId;
    }
}
