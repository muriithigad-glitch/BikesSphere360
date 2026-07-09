package com.deepseek.bikesphere360.navigations

const val ROUTE_SPLASH = "splash"
const val ROUTE_LOGIN = "login"
const val ROUTE_REGISTER = "register"
const val ROUTE_HOME = "home"
const val ROUTE_ADD_PRODUCT = "add_product"
const val ROUTE_HOT_DEALS = "hot_deals"
const val ROUTE_SETTINGS = "settings"
const val ROUTE_PROFILE = "profile"
const val ROUTE_ABOUT_US = "about_us"
const val ROUTE_CONTACT_US = "contact_us"
const val ROUTE_REPORT_PROBLEM = "report_problem"
const val ROUTE_ADMIN_DASHBOARD = "admin_dashboard"
const val ROUTE_CHECKOUT = "checkout/{productId}/{productName}/{price}"
const val ROUTE_BOOK_APPOINTMENT = "book_appointment/{productId}/{productName}"

// Category routes with arguments
const val ROUTE_VIEW_PRODUCTS = "view_products/{type}/{subCategory}"
fun createViewProductsRoute(type: String, subCategory: String) = "view_products/$type/$subCategory"
