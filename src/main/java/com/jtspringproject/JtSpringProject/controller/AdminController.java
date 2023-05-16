package com.jtspringproject.JtSpringProject.controller;

import java.sql.*;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.jtspringproject.JtSpringProject.models.Category;
import com.jtspringproject.JtSpringProject.models.Product;
import com.jtspringproject.JtSpringProject.models.User;
import com.jtspringproject.JtSpringProject.services.categoryService;
import com.jtspringproject.JtSpringProject.services.productService;
import com.jtspringproject.JtSpringProject.services.userService;
import com.mysql.cj.protocol.Resultset;

import net.bytebuddy.asm.Advice.This;
import net.bytebuddy.asm.Advice.OffsetMapping.ForOrigin.Renderer.ForReturnTypeName;

@Controller
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private userService userService;
	@Autowired
	private categoryService categoryService;
	
	@Autowired
	private productService productService;
	
	int adminlogcheck = 0;
	String usernameforclass = "";
	@RequestMapping(value = {"/","/logout"})
	public String returnIndex() {
		adminlogcheck =0;
		usernameforclass = "";
		return "userLogin";
	}
	
	
	
	@GetMapping("/index")
	public String index(Model model) {
		if(usernameforclass.equalsIgnoreCase(""))
			return "userLogin";
		else {
			model.addAttribute("username", usernameforclass);
			return "index";
		}
			
	}
	
	
	@GetMapping("/")
	public String adminlogin() {
		
		return "adminlogin";
	}
	@GetMapping("/adminhome")
	public String adminHome(Model model) {
		if(adminlogcheck!=0)
			return "adminHome";
		else
			return "redirect:/admin";
	}
	@GetMapping("/loginvalidate")
	public String adminlog(Model model) {
		
		return "adminlogin";
	}
	@RequestMapping(value = "loginvalidate", method = RequestMethod.POST)
	public ModelAndView adminlogin( @RequestParam("username") String username, @RequestParam("password") String pass) {
		
		User user=this.userService.checkLogin(username, pass);
		
		if(user.getRole().equals("ROLE_ADMIN")) {
			ModelAndView mv = new ModelAndView("adminHome");
			mv.addObject("admin", user);
			return mv;
		}
		else {
			ModelAndView mv = new ModelAndView("adminlogin");
			mv.addObject("msg", "Please enter correct username and password");
			return mv;
		}
	}
	@GetMapping("categories")
	public ModelAndView getcategory() {
		ModelAndView mView = new ModelAndView("categories");
		List<Category> categories = this.categoryService.getCategories();
		mView.addObject("categories",categories);
		return mView;
	}
	@RequestMapping(value = "sendcategory",method = RequestMethod.POST)
	public ModelAndView addCategory(@RequestParam("categoryname") String category_name)
	{
		System.out.println(category_name);
		
		Category category =  this.categoryService.addCategory(category_name);
		if(category.getName().equals(category_name)) {
			ModelAndView mView = new ModelAndView("admin/categories");
			mView.addObject("msg", "Category added successfully");
			return mView;
		}else {
			ModelAndView mView = new ModelAndView("admin/categories");
			mView.addObject("msg", "failed to add category");
			return mView;
		}
	}
	
	@GetMapping("/admin/categories/delete")
	public ModelAndView removeCategoryDb(@RequestParam("id") int id)
	{	
			this.categoryService.deleteCategory(id);
			ModelAndView mView = new ModelAndView("admin/categories");
			return mView;
	}
	
	@GetMapping("/admin/categories/update")
	public ModelAndView updateCategoryDb(@RequestParam("categoryid") int id, @RequestParam("categoryname") String categoryname)
	{
		Category category = this.categoryService.updateCategory(id, categoryname);
		ModelAndView mView = new ModelAndView("admin/categories");
		return mView;
	}

	
//	 --------------------------Remaining --------------------
	@GetMapping("products")
	public ModelAndView getproduct() {
		
		ModelAndView mView = new ModelAndView("products");
		
		List<Product> products = this.productService.getProducts();
		
		if(products.isEmpty()) {
			mView.addObject("msg","No products are available");
		}else {
			mView.addObject("products",products);	
		}
		
		return mView;
	}
	@GetMapping("products/add")
	public String addproduct() {
		return "productsAdd";
	}

	@RequestMapping(value = "products/add",method=RequestMethod.POST)
	public ModelAndView addProduct(@ModelAttribute Product product) {
		System.out.println(product.getDescription());
		
		ModelAndView mView = new ModelAndView("products");
		
		List<Product> products = this.productService.getProducts();
		mView.addObject("products",products);
		
		return mView;
	}
	
	
	@GetMapping("products/update")
	public String updateproduct(@RequestParam("pid") int id,Model model) {
		String pname,pdescription,pimage;
		int pid,pprice,pweight,pquantity,pcategory;
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ecommjava","root","");
			Statement stmt = con.createStatement();
			Statement stmt2 = con.createStatement();
			ResultSet rst = stmt.executeQuery("select * from products where id = "+id+";");
			
			if(rst.next())
			{
			pid = rst.getInt(1);
			pname = rst.getString(2);
			pimage = rst.getString(3);
			pcategory = rst.getInt(4);
			pquantity = rst.getInt(5);
			pprice =  rst.getInt(6);
			pweight =  rst.getInt(7);
			pdescription = rst.getString(8);
			model.addAttribute("pid",pid);
			model.addAttribute("pname",pname);
			model.addAttribute("pimage",pimage);
			ResultSet rst2 = stmt.executeQuery("select * from categories where categoryid = "+pcategory+";");
			if(rst2.next())
			{
				model.addAttribute("pcategory",rst2.getString(2));
			}
			model.addAttribute("pquantity",pquantity);
			model.addAttribute("pprice",pprice);
			model.addAttribute("pweight",pweight);
			model.addAttribute("pdescription",pdescription);
			}
		}
		catch(Exception e)
		{
			System.out.println("Exception:"+e);
		}
		return "productsUpdate";
	}
	@RequestMapping(value = "products/updateData",method=RequestMethod.POST)
	public String updateproducttodb(@RequestParam("id") int id,@RequestParam("name") String name, @RequestParam("price") int price, @RequestParam("weight") int weight, @RequestParam("quantity") int quantity, @RequestParam("description") String description, @RequestParam("productImage") String picture ) 
	
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ecommjava","root","");
			
			PreparedStatement pst = con.prepareStatement("update products set name= ?,image = ?,quantity = ?, price = ?, weight = ?,description = ? where id = ?;");
			pst.setString(1, name);
			pst.setString(2, picture);
			pst.setInt(3, quantity);
			pst.setInt(4, price);
			pst.setInt(5, weight);
			pst.setString(6, description);
			pst.setInt(7, id);
			int i = pst.executeUpdate();			
		}
		catch(Exception e)
		{
			System.out.println("Exception:"+e);
		}
		return "redirect:/admin/products";
	}
	
	@GetMapping("products/delete")
	public String removeProductDb(@RequestParam("id") int id)
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ecommjava","root","");
			
			
			PreparedStatement pst = con.prepareStatement("delete from products where id = ? ;");
			pst.setInt(1, id);
			int i = pst.executeUpdate();
			
		}
		catch(Exception e)
		{
			System.out.println("Exception:"+e);
		}
		return "redirect:/admin/products";
	}
	
	@PostMapping("/admin/products")
	public String postproduct() {
		return "redirect:/admin/categories";
	}
	
	@GetMapping("/admin/customers")
	public String getCustomerDetail() {
		return "displayCustomers";
	}
	
	
	@GetMapping("profileDisplay")
	public String profileDisplay(Model model) {
		String displayusername,displaypassword,displayemail,displayaddress;
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ecommjava","root","");
			Statement stmt = con.createStatement();
			ResultSet rst = stmt.executeQuery("select * from users where username = '"+usernameforclass+"';");
			
			if(rst.next())
			{
			int userid = rst.getInt(1);
			displayusername = rst.getString(2);
			displayemail = rst.getString(3);
			displaypassword = rst.getString(4);
			displayaddress = rst.getString(5);
			model.addAttribute("userid",userid);
			model.addAttribute("username",displayusername);
			model.addAttribute("email",displayemail);
			model.addAttribute("password",displaypassword);
			model.addAttribute("address",displayaddress);
			}
		}
		catch(Exception e)
		{
			System.out.println("Exception:"+e);
		}
		System.out.println("Hello");
		return "updateProfile";
	}
	
	@RequestMapping(value = "updateuser",method=RequestMethod.POST)
	public String updateUserProfile(@RequestParam("userid") int userid,@RequestParam("username") String username, @RequestParam("email") String email, @RequestParam("password") String password, @RequestParam("address") String address) 
	
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ecommjava","root","");
			
			PreparedStatement pst = con.prepareStatement("update users set username= ?,email = ?,password= ?, address= ? where uid = ?;");
			pst.setString(1, username);
			pst.setString(2, email);
			pst.setString(3, password);
			pst.setString(4, address);
			pst.setInt(5, userid);
			int i = pst.executeUpdate();	
			usernameforclass = username;
		}
		catch(Exception e)
		{
			System.out.println("Exception:"+e);
		}
		return "redirect:/index";
	}

}
