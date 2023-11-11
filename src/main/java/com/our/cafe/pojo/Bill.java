package com.our.cafe.pojo;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

@NamedQuery(name = "Bill.getBills", query = "select b from Bill b order by b.id desc")
@NamedQuery(name = "Bill.getBillByUsername", query = "select b from Bill b where b.createdBy=:username order by b.id desc")

@Table(name = "bill")
@Data
@Entity
@DynamicInsert
@DynamicUpdate
public class Bill implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	private String uuid;
    private String name;
    private String email;
    private String contactNumber;
    private String paymentMethod;
    private Integer total;
    @Column(name = "product_detail", columnDefinition = "json")
    private String productDetails;
    private String createdBy;
    public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getContactNumber() {
		return contactNumber;
	}
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public String getProductDetail() {
		return productDetails;
	}
	public void setProductDetail(String productDetail) {
		this.productDetails = productDetail;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

}
