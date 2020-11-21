-- phpMyAdmin SQL Dump
-- version 4.5.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Nov 21, 2020 at 05:48 PM
-- Server version: 10.1.13-MariaDB
-- PHP Version: 5.6.23

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `shopmanager`
--

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `category_save` (INOUT `in_category_id` BIGINT, IN `in_category_parent_id` BIGINT, IN `in_description` VARCHAR(128))  begin
	
	if in_category_id is null or in_category_id = 0 then
		insert into itemcategory (itemcategory.`categoryParentId` , itemcategory.description)
			values (in_category_parent_id , in_description);
		set in_category_id = last_insert_id();
	else
		update itemcategory set `categoryParentId` = in_category_parent_id , `description` = in_description
			where itemcategory.`categoryId` = in_category_id;
	end if;
	
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `contact_save` (INOUT `in_contact_id` BIGINT, IN `in_national_id` BIGINT, IN `in_address` TEXT, IN `in_name` VARCHAR(128), IN `in_lastname` VARCHAR(128), IN `in_fathername` VARCHAR(128), IN `in_phone` VARCHAR(128))  begin
	
	declare v_reg_date bigint default 139901010000;
	
	if in_contact_id is null or in_contact_id = 0 then
		INSERT INTO shopmanager.contact
			(`nationalId`, address, `regDate`, name, lastname, fathername, full_info , phone)
				values
			(in_national_id, in_address, v_reg_date, in_name, in_lastname, in_fathername, REPLACE(concat(in_name , in_lastname), " ", "") , in_phone);
		
		set in_contact_id = last_insert_id();
	else 
		UPDATE shopmanager.contact
		SET `nationalId`=in_national_id, address=in_address, name=in_name, lastname=in_lastname, fathername=in_fathername, 
		full_info = REPLACE(concat(in_name , in_lastname , in_fathername), " ", "") , phone = in_phone
			WHERE `contactId`=in_contact_id;
	end if;


END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `get_item_stock` (IN `in_item_id` BIGINT, OUT `out_stock` DECIMAL)  begin
	select sum(`suAmount`) into out_stock from invoicedetail where `itemId` = in_item_id;
	if out_stock is null then
		set out_stock = 0;
	end if;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `invoicedetail_delete` (IN `in_invoice` BIGINT)  begin
	delete from invoice where invoiceid = in_invoice;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `invoicedetail_insert` (IN `in_date` BIGINT, IN `in_contact` BIGINT, IN `in_operation` INT, IN `in_item` BIGINT, IN `in_su_amount` DECIMAL, IN `in_su_price` DECIMAL, IN `in_ref_id` BIGINT, IN `in_invoice` BIGINT)  begin
	
	INSERT INTO shopmanager.invoicedetail
		(`date`, `contactId`, `operationType`, `itemId`, `suAmount`, `suPrice`, `refDetailId`, invoiceid)
	VALUES(in_date, in_contact, in_operation, in_item, in_su_amount, in_su_price, in_ref_id, in_invoice);

END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `invoice_delete` (IN `in_invoice_id` BIGINT)  begin
	
	declare detailId BIGINT;

	declare operation_type INT;

	declare lv_result INT;


	DECLARE finished INTEGER DEFAULT 0;

	DEClARE curs
		CURSOR FOR 
			SELECT invoicedetail.`detailId`,invoicedetail.`operationType` FROM invoicedetail where invoicedetail.invoiceid = in_invoice_id;
	DECLARE CONTINUE HANDLER 
        FOR NOT FOUND SET finished = 1;
       
    OPEN curs;

	getEmail: LOOP
		FETCH curs INTO detailId , operation_type;
		IF finished = 1 THEN 
			LEAVE getEmail;
		END IF;
	
		if operation_type = 10 then
			call shopmanager.invoicedetail_buy_delete(detailId, lv_result);
		elseif operation_type = 11 then
			call shopmanager.invoicedetail_refund_buy_delete(detailId, lv_result);
		elseif operation_type = 20 then
			call shopmanager.invoicedetail_sell_delete(detailId, lv_result);
		elseif operation_type = 21 then
			call shopmanager.invoicedetail_refund_sell_delete(detailId, lv_result);
		end if;
	END LOOP getEmail;

	CLOSE curs;
       
	delete from invoice where invoice.invoiceid = in_invoice_id;

	call `shopmanager`.`payment_delete`(null , in_invoice_id);

END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `invoice_insert` (IN `in_date` BIGINT, IN `in_contact` BIGINT, IN `in_type` INT, IN `in_total_cost` DECIMAL, OUT `out_invoice_id` BIGINT)  begin
	
	insert into invoice 
		(invoice.invoicedate , invoice.contact , invoice.operationtype , invoice.totalCost ) 
		values 
		(in_date , in_contact , in_type , in_total_cost);
	
	set out_invoice_id = last_insert_id();
	
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `invoice_update` (IN `in_invoice_id` BIGINT, IN `in_date` BIGINT, IN `in_contact` BIGINT, IN `in_type` INT, IN `in_total_cost` DECIMAL)  begin
	update invoice set 
		invoice.invoicedate = in_date, invoice.contact = in_contact, invoice.operationtype = in_type, invoice.totalCost = in_total_cost 
		where invoice.invoiceid = in_invoice_id;
	
	delete from invoicedetail where invoiceid = in_invoice_id;
	delete from payments where `refId` = in_invoice_id and `refType` = 1;

END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `item_price_update` (IN `in_item_id` BIGINT, IN `in_price_type` INT, IN `in_price` DECIMAL, IN `in_date` BIGINT, IN `in_delete` BOOL)  begin
	if in_delete then
		delete from item_price where item_id = in_item_id and item_price.`date` = in_date and item_price.price_type = in_price_type;
	else
		replace into item_price (item_price.price_type , item_price.`date` , item_price.item_id , item_price.price)
			values (in_price_type , in_date , in_item_id , in_price);
	end if;
end$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `item_save` (IN `in_item_id` BIGINT, IN `in_category_id` BIGINT, IN `in_description` VARCHAR(50), IN `in_unit_1` INT, IN `in_ratio_1` DECIMAL, IN `in_unit_2` INT)  begin

	declare max_id_in_category bigint;
	
	if in_item_id = 0 or in_item_id is null then
	
		SELECT max(itemId) into max_id_in_category FROM `item` WHERE categoryId = in_category_id;
		
		if max_id_in_category is null then
			set max_id_in_category = in_category_id * 100 + 1;
		else
			set max_id_in_category = max_id_in_category + 1;
		end if;
		
		
		if max_id_in_category > (in_category_id * 100) + 99 then
			signal sqlstate '45000' set MESSAGE_TEXT = 'item category is full';
		else 
			set in_item_id = max_id_in_category;
			INSERT INTO shopmanager.item (`itemId`, `categoryId`, `description`, `unit1`, `ratio1`, `unit2`)
				VALUES(in_item_id, in_category_id, in_description, in_unit_1, in_ratio_1, in_unit_2);
		end if;
	else
		UPDATE shopmanager.item
			SET `categoryId`=in_category_id, description=in_description, unit1=in_unit_1, ratio1=in_ratio_1, unit2=in_unit_2
		WHERE `itemId`=in_item_id;
	end if;


	

	
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `payment_delete` (IN `document` BIGINT, IN `invoice` BIGINT)  begin
	if document = null then
		set document = -1;
	end if;

	if invoice = null then
		set invoice = -1;
	end if;
	
	delete from payments where `documentId` = document or `invoiceId` = invoice;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `payment_insert` (IN `reftype` INT, IN `refid` BIGINT, IN `contact` BIGINT, IN `creditor` DECIMAL, IN `debtor` DECIMAL, IN `object_id` BIGINT, IN `object_type` INT, IN `in_date` BIGINT)  BEGIN
	
	insert into payments 
	( `reftype` , `refId` , `contactId`,`creditor` , `debtor`,`objectId`,`objectType` ,`date`) 
	values 
	(reftype , refid , contact , creditor , debtor , object_id , object_type , in_date);
	
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `payment_update` (IN `document` BIGINT, IN `invoice` BIGINT, IN `contact` BIGINT, IN `creditor` DECIMAL, IN `debtor` DECIMAL, IN `object_id` BIGINT, IN `object_type` INT)  BEGIN
	
	call `shopmanager`.`payment_delete` (document , invoice);

	call `shopmanager`.`payment_insert`(document , invoice, contact , creditor , debtor , object_id , object_type );
	
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `report_customer_item_bill` (IN `in_date_start` BIGINT, IN `in_date_end` BIGINT, IN `in_contact_id` BIGINT, IN `in_item_id` BIGINT, IN `in_operation` INT)  begin
	
	select `invoiceid`, date , invoicedetail.`contactId` , trim(concat(contact.name ," ", contact.lastname)), `operationType` , item.`itemId` , item.description, suAmount , unit.`unitName` , suPrice,  `suAmount` * `suPrice` as totalPrice from invoicedetail 
	join item on invoicedetail.`itemId` = item.`itemId`
	join unit on item.unit1 = unit.`unitId`
	join contact on contact.`contactId` = invoicedetail.`contactId`
		where 
		invoicedetail.`contactId` = (case when in_contact_id is null then invoicedetail.`contactId` else in_contact_id end)
		and
		invoicedetail.`itemId` = (case when in_item_id is null then invoicedetail.`itemId` else in_item_id end)
		and 
		`operationType` = (case when in_operation is null then `operationType` else in_operation end)
		and
		`date` >= (case when in_date_start is null then 0 else in_date_start end)
		and
		`date` <= (case when in_date_end is null then ~0 else in_date_end end);
	
	
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `report_item_price` (IN `in_item_id` BIGINT)  BEGIN
	select * from item_price ip where ip.item_id = in_item_id order by `date` desc; 
	END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `report_payment_bill` (IN `in_contact_id` BIGINT, IN `in_date_start` BIGINT, IN `in_date_end` BIGINT)  BEGIN
	select `refType`, `refId`,max(p.`date`) , concat(c.`contactId`," - ", trim(concat(c.name," ",c.lastname))) as cName, 
		sum(p.creditor),sum(p.debtor), sum(creditor)-sum(debtor) 
		from payments p 
		join contact c on p.`contactId` = c.`contactId` 
		where 
		c.`contactId` = (case when in_contact_id is null then c.`contactId` else in_contact_id end)
		and
		p.`date` >= (case when in_date_start is null then 0 else in_date_start end)
		and
		p.`date` <= (case when in_date_end is null then ~0 else in_date_end end)
	group by `refId` ;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `search_contact` (IN `in_pattern` VARCHAR(256))  begin
	
	select * from contact c where c.full_info like concat('%',in_pattern,'%') or c.full_info like concat('%',replace(in_pattern , " ",""),'%') limit 10;
	
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `search_contact_id` (IN `in_contact_id` BIGINT)  begin
	
	select * from contact c where `contactId` = in_contact_id;
	
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `search_contact_item` (IN `in_contactId` BIGINT, IN `in_itemId` BIGINT, IN `in_operation` INT)  begin
	
		select * from invoicedetail
		where `contactId` = in_contactId and `itemId` = in_itemId and `operationType` = in_operation order by `date` desc limit 5 ;
	


END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `search_invoice` (IN `in_invoice_id` BIGINT)  begin	
	select * from invoice where invoiceid = in_invoice_id;
	select * from invoicedetail where invoiceid = in_invoice_id;
	select * from payments where `refId` = in_invoice_id and `refType` = 1;
	
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `search_item` (IN `in_pattern` VARCHAR(256))  begin
	select * from item i where i.description like CONCAT('%', in_pattern , '%') limit 10;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `search_item_id` (IN `in_item_id` BIGINT)  begin
/*	declare v_max_item_id bigint;
	set v_max_item_id = FLOOR(in_item_id/100)*100 + 99;

	select * from item where `itemId` == in_item_id and `itemId` <= v_max_item_id limit 5;*/
	select * from item where `itemId` = in_item_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `select_catalog` ()  begin
	select itemid , item.`categoryId` , item.description as itemDesc , unit1 , ratio1 , unit2 , itemcategory.description as catDesc  
			from item left join itemcategory 
				on itemcategory.`categoryId` = item.`categoryId`;

END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `select_category_all_leaf` ()  begin
	SELECT * from itemcategory where categoryId not in (SELECT categoryParentId FROM `itemcategory` WHERE categoryParentId is not null) order by itemcategory.description;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `select_unit_all` ()  begin
	select * from unit u;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `bank`
--

CREATE TABLE `bank` (
  `bankId` int(11) NOT NULL,
  `bankName` varchar(20) COLLATE utf8_persian_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_persian_ci;

--
-- Dumping data for table `bank`
--

INSERT INTO `bank` (`bankId`, `bankName`) VALUES
(1010, 'اقتصاد نوین'),
(1009, 'انصار'),
(1008, 'پارسیان'),
(1032, 'پاسارگاد'),
(1016, 'پست بانک'),
(1003, 'تجارت'),
(1015, 'توسعه تعاون'),
(1014, 'توسعه صادرات'),
(1004, 'رفاه'),
(1013, 'سپه'),
(1031, 'سرمایه'),
(1007, 'شهر'),
(1006, 'صادرات'),
(1033, 'قوامین'),
(1011, 'کشاورزی'),
(1020, 'گردشگری'),
(1012, 'مسکن'),
(1002, 'ملت'),
(1001, 'ملی');

-- --------------------------------------------------------

--
-- Table structure for table `bankaccount`
--

CREATE TABLE `bankaccount` (
  `bankAccountId` bigint(20) NOT NULL,
  `bankId` int(11) NOT NULL,
  `branchId` int(11) NOT NULL,
  `accountInfo` text COLLATE utf8_persian_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_persian_ci;

--
-- Dumping data for table `bankaccount`
--

INSERT INTO `bankaccount` (`bankAccountId`, `bankId`, `branchId`, `accountInfo`) VALUES
(10010101, 1001, 100101, '4687215;5986457125348546'),
(10020101, 1002, 100201, '65978754;6104337942491078');

-- --------------------------------------------------------

--
-- Table structure for table `bankbranch`
--

CREATE TABLE `bankbranch` (
  `branchId` int(11) NOT NULL,
  `branchInfo` text COLLATE utf8_persian_ci NOT NULL,
  `bankId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_persian_ci;

--
-- Dumping data for table `bankbranch`
--

INSERT INTO `bankbranch` (`branchId`, `branchInfo`, `bankId`) VALUES
(100101, 'مرکزی', 1001),
(100102, 'کمربندی', 1001),
(100201, 'مرکزی', 1002),
(100202, 'امام خمینی', 1002);

-- --------------------------------------------------------

--
-- Table structure for table `chequebook`
--

CREATE TABLE `chequebook` (
  `chequeBookId` bigint(20) NOT NULL,
  `bankAccountId` bigint(20) NOT NULL,
  `serialFirst` int(11) NOT NULL,
  `serialLast` int(11) NOT NULL,
  `info` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_persian_ci;

-- --------------------------------------------------------

--
-- Table structure for table `config`
--

CREATE TABLE `config` (
  `configid` varchar(128) COLLATE utf8_persian_ci DEFAULT NULL,
  `value` varchar(128) COLLATE utf8_persian_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_persian_ci;

--
-- Dumping data for table `config`
--

INSERT INTO `config` (`configid`, `value`) VALUES
('inventory_method', 'FIFO');

-- --------------------------------------------------------

--
-- Table structure for table `contact`
--

CREATE TABLE `contact` (
  `contactId` bigint(20) UNSIGNED NOT NULL,
  `nationalId` bigint(20) DEFAULT NULL,
  `address` text COLLATE utf8_persian_ci,
  `regDate` bigint(20) NOT NULL,
  `name` varchar(100) COLLATE utf8_persian_ci DEFAULT NULL,
  `lastname` varchar(100) COLLATE utf8_persian_ci DEFAULT NULL,
  `fathername` varchar(100) COLLATE utf8_persian_ci DEFAULT NULL,
  `full_info` varchar(300) COLLATE utf8_persian_ci DEFAULT NULL COMMENT 'combination of name , lastname and fathername',
  `phone` varchar(100) COLLATE utf8_persian_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_persian_ci;

--
-- Dumping data for table `contact`
--

INSERT INTO `contact` (`contactId`, `nationalId`, `address`, `regDate`, `name`, `lastname`, `fathername`, `full_info`, `phone`) VALUES
(1105, 2420517679, 'مرودشت', 13990506, 'علی', 'حمزه', 'علی', 'علیحمزهعلی', ''),
(1106, 6548751235, 'مرودشت', 13990506, 'جواد', 'عمادی', 'حمیدرضا', 'جوادعمادی', NULL),
(1108, 8754876515, 'مرودشت', 13990506, 'محمد جواد', 'عمادی', 'غلام', 'محمدجوادعمادی', NULL),
(1110, 48576877, 'مرودشت - خیابان 8 تیر', 139901010000, 'علی', 'مرادی', 'علی', 'علیمرادیعلی', '09362370299');

-- --------------------------------------------------------

--
-- Table structure for table `invoice`
--

CREATE TABLE `invoice` (
  `invoiceid` bigint(20) UNSIGNED NOT NULL,
  `invoicedate` bigint(20) DEFAULT NULL,
  `contact` bigint(20) UNSIGNED DEFAULT NULL,
  `operationtype` int(11) DEFAULT NULL,
  `totalCost` decimal(16,4) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_persian_ci;

--
-- Dumping data for table `invoice`
--

INSERT INTO `invoice` (`invoiceid`, `invoicedate`, `contact`, `operationtype`, `totalCost`) VALUES
(20, 13990829, 1105, 10, '100.0000'),
(21, 13990829, 1106, 20, '300.0000'),
(22, 13990829, 1105, 10, '3600.0000'),
(23, 13990829, 1105, 10, '1500.0000'),
(24, 13990830, 1105, 10, '20000.0000'),
(25, 13990830, 1106, 20, '20000.0000');

-- --------------------------------------------------------

--
-- Table structure for table `invoicedetail`
--

CREATE TABLE `invoicedetail` (
  `detailId` bigint(20) UNSIGNED NOT NULL,
  `date` bigint(20) UNSIGNED NOT NULL,
  `contactId` bigint(20) UNSIGNED DEFAULT NULL,
  `operationType` int(11) DEFAULT NULL,
  `itemId` bigint(20) UNSIGNED DEFAULT NULL,
  `suAmount` decimal(18,9) DEFAULT NULL COMMENT 'smallest unit amount',
  `suPrice` decimal(16,4) DEFAULT NULL,
  `refDetailId` bigint(20) DEFAULT NULL,
  `invoiceid` bigint(20) UNSIGNED DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_persian_ci;

--
-- Dumping data for table `invoicedetail`
--

INSERT INTO `invoicedetail` (`detailId`, `date`, `contactId`, `operationType`, `itemId`, `suAmount`, `suPrice`, `refDetailId`, `invoiceid`) VALUES
(4525, 13990829, 1106, 20, 44052, '-1.000000000', '300.0000', NULL, 21),
(4526, 13990829, 1105, 10, 44052, '1.000000000', '100.0000', NULL, 20),
(4527, 13990829, 1105, 10, 201049, '12.000000000', '300.0000', NULL, 22),
(4529, 13990829, 1105, 10, 201210, '15.000000000', '100.0000', NULL, 23),
(4530, 13990830, 1105, 10, 201213, '10.000000000', '2000.0000', NULL, 24),
(4531, 13990830, 1106, 20, 201213, '-5.000000000', '4000.0000', NULL, 25);

-- --------------------------------------------------------

--
-- Table structure for table `item`
--

CREATE TABLE `item` (
  `itemId` bigint(20) UNSIGNED NOT NULL,
  `categoryId` bigint(20) DEFAULT NULL,
  `description` varchar(50) COLLATE utf8_persian_ci DEFAULT NULL,
  `unit1` int(11) DEFAULT NULL,
  `ratio1` decimal(18,9) DEFAULT NULL,
  `unit2` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_persian_ci;

--
-- Dumping data for table `item`
--

INSERT INTO `item` (`itemId`, `categoryId`, `description`, `unit1`, `ratio1`, `unit2`) VALUES
(44052, 130101, 'آچار یکسر رینگ 13', 5, NULL, NULL),
(44053, 130101, 'آچار یکسر رینگ 30', 5, NULL, NULL),
(201023, 130202, 'الکترو پمپ 1 اسب 1 اينچ', 3, NULL, NULL),
(201049, 130202, 'ماشين حساب کاتيگا 2383/14', 5, NULL, NULL),
(201125, 130202, 'شير مخلوط پدالي روشويي', 5, NULL, NULL),
(201132, 130202, 'تيغه ذوزنقه اي کاتر1050', 5, NULL, NULL),
(201202, 130202, 'سشوار صنعتي', 3, NULL, NULL),
(201203, 130202, 'دستگاه مدل UAw967با پايه فلزي', 5, NULL, NULL),
(201209, 130202, 'فشار سنج مدل BM20', 5, NULL, NULL),
(201210, 130202, 'اسپيلت 12000 گوي مدل اس فور ماتيک', 3, NULL, NULL),
(201211, 130202, 'اسپيلت 24 هزار مدل اس فور ماتيک', 3, NULL, NULL),
(201212, 130202, 'هويه هواي گرم 952 معمولي', 5, NULL, NULL),
(201213, 130202, 'کف چين 8PK-101K', 5, NULL, NULL),
(201214, 130202, 'پنس دوقلو 908-T301', 5, NULL, NULL),
(201215, 130101, 'پنس TST 15', 5, NULL, NULL),
(201216, 130101, 'پنس TST10', 5, NULL, NULL),
(201217, 130101, 'موتور WEM 1/5', 3, NULL, NULL),
(201218, 130101, 'اسپيلت ديواري 30 هزارکم مصرف لارس', 3, NULL, NULL),
(201220, 130101, 'ساعت LED سايز 48*12', 5, NULL, NULL),
(201221, 130101, 'تلويزيون LG 55 اينچ LED با پايه', 3, NULL, NULL),
(201222, 130101, 'شير توپي 2 اينچ استيل با اکچيويتور 75', 5, NULL, NULL),
(201223, 130101, 'الکتروپمپ 1/2*1 پلي اتيلن 1/1 کيلو وات', 3, NULL, NULL),
(201224, 130101, 'کلمپ متر 870N -AC/Dc', 3, NULL, NULL),
(201225, 130101, 'ميني فرز هيتاچي 730 وات', 3, NULL, NULL),
(201226, 130101, 'دستگاه ضد عفوني کننده پدالي دست', 3, NULL, NULL),
(201227, 130101, 'کارتن دوز لوک ژاپن Fm-15', 3, NULL, NULL),
(201228, 130101, 'گريس پمپ سطلي 20 کيلويي', 5, NULL, NULL),
(401006, 130101, 'کابل شو 6*6', 5, NULL, NULL),
(401014, 130101, 'کنتاکتور 50 امپر', 5, NULL, NULL),
(401022, 130101, 'کمپکت 250 امپر', 5, NULL, NULL),
(401027, 130101, 'سرواير 6', 5, NULL, NULL),
(401057, 130101, 'سه راهه صنعتي', 5, NULL, NULL),
(401058, 130101, 'چراغ خياباني 250W سديم گل نور', 5, NULL, NULL),
(401063, 130101, 'محافظ کامپيوتر', 5, NULL, NULL),
(401068, 130101, 'کنتاکتورD18', 5, NULL, NULL),
(401070, 130101, 'لامپ 24V60W', 5, NULL, NULL),
(401071, 130101, 'زغال ميني فرز', 5, NULL, NULL),
(401097, 130101, 'تلفن معمولي', 5, NULL, NULL),
(401100, 130101, 'شاسي تله قارچي قفل شو', 5, NULL, NULL),
(401107, 130101, 'المنت 750', 5, NULL, NULL),
(401110, 130101, 'گلند 36', 5, NULL, NULL),
(401116, 130101, 'سيم افشان 2/5', 8, NULL, NULL),
(401125, 130101, 'کليد حرارتي 6-4', 5, NULL, NULL),
(401135, 130101, 'فيوز تکفاز 4 آمپر', 5, NULL, NULL),
(401136, 130101, 'فن تابلو', 5, NULL, NULL),
(401137, 130101, 'فيلتر فن تابلو', 5, NULL, NULL),
(401166, 130101, 'واير شو 10', 5, NULL, NULL),
(401175, 130101, 'گلند شلاقي 16', 5, NULL, NULL),
(401197, 130101, 'تبديل 2به 3', 5, NULL, NULL),
(401205, 130101, 'پلاتين  LA5FH431', 5, NULL, NULL),
(401213, 130101, 'لامپ 250 بخار', 5, NULL, NULL),
(401214, 130101, 'چراغ خياباني 125 وات', 5, NULL, NULL),
(401247, 130101, 'سيم افشان 1/5', 8, NULL, NULL),
(401328, 130101, 'زنگ اخبار', 5, NULL, NULL),
(401340, 130101, 'ميکروسوئيچ آنتني', 5, NULL, NULL),
(401356, 130101, 'پايه بست کمربندي بزرگ', 5, NULL, NULL),
(401357, 130101, 'پايه بست کمربندي کوچک', 5, NULL, NULL),
(401361, 130101, 'دمپر ديگ بخار', 3, NULL, NULL),
(401363, 130101, 'تلفن پاناسونيک 3611', 5, NULL, NULL),
(401384, 130101, 'کليد حرارتي10-6 آمپر', 5, NULL, NULL),
(401412, 130101, 'لامپ 18 وات', 5, NULL, NULL),
(401428, 130101, 'کليد کولر', 5, NULL, NULL),
(401468, 130101, 'مرکز کنترل 3 رله', 5, NULL, NULL),
(401479, 130101, 'تايمر آتونيکس و پايه', 5, NULL, NULL),
(401481, 130101, 'وايرشو دوبل 1/5', 5, NULL, NULL),
(401490, 130101, 'وارنيش 5', 8, NULL, NULL),
(401491, 130101, 'وارنيش 10', 8, NULL, NULL),
(401492, 130101, 'وارنيش 12', 8, NULL, NULL),
(401512, 130101, 'سيم ارت مسي', 6, NULL, NULL),
(401513, 130101, 'AAA', 1, '5.000000000', 5),
(401623, 130202, 'لوله شلاقي 16 روکشدار', 8, NULL, NULL),
(401662, 130202, 'لامپ 30 وات LED', 5, NULL, NULL),
(401667, 130202, 'لامپ 40 وات LED', 5, NULL, NULL),
(401674, 130202, 'لامپ 125 وات جيوه اي', 5, NULL, NULL),
(401723, 130202, 'چراغ سيگنال سبز', 5, NULL, NULL),
(401728, 130202, 'لامپ 50 وات SMD', 5, NULL, NULL),
(401730, 130202, 'لامپ 12 وات LED', 5, NULL, NULL),
(401736, 130202, 'نوار فرم سيم 12', 1, NULL, NULL),
(401737, 130202, 'رله شکوه مدل 701 گازوئيلي', 5, NULL, NULL),
(401738, 130202, 'پايه رله شکوه', 5, NULL, NULL),
(401739, 130202, 'لول سوئيچ LS -02عيوض', 5, NULL, NULL),
(401740, 130202, 'ليميت سوئيچ باکس MeGa valVE', 5, NULL, NULL),
(401741, 130202, 'چراغ ايزولا 120 ضدنم و غبار 37 وات', 5, NULL, NULL),
(401743, 130202, 'لامپ 150وات مدادي', 5, NULL, NULL),
(401744, 130202, 'لامپ 33 وات', 5, NULL, NULL),
(401745, 130202, 'لامپ 250 وات جيوه', 5, NULL, NULL),
(401749, 130202, 'خازن 300MK', 5, NULL, NULL),
(401750, 130202, 'وارنيش 14', 8, NULL, NULL),
(401751, 130202, 'الکتروگيربکس 7/5 کيلووات400 دور', 3, NULL, NULL),
(401752, 130202, 'الکتروگيربکس 1/1 کيلووات 400 دور', 3, NULL, NULL),
(401753, 130202, 'لامپ آکواريوم 100', 5, NULL, NULL),
(402008, 130202, 'سيم جوش استيل 2/5', 6, NULL, NULL),
(402022, 130202, 'پيچ متري استيل 12', 5, NULL, NULL),
(402030, 130202, 'پکينگ', 5, NULL, NULL),
(402044, 130202, 'ميخ پرچ 4', 5, NULL, NULL),
(402045, 130202, 'ميخ پرچ 5', 5, NULL, NULL),
(402057, 130202, 'پيچ 8 5 سانتي', 5, NULL, NULL),
(402059, 130202, 'پيچ 8 3 سانتي', 5, NULL, NULL),
(402097, 130202, 'واشر تخت 6', 5, NULL, NULL),
(402106, 130202, 'مهره 10', 5, NULL, NULL),
(402163, 130202, 'ياتاقان p205', 5, NULL, NULL),
(402167, 130202, 'بلبرينگ 6010', 5, NULL, NULL),
(402182, 130202, 'لاستيک ضربه گير', 5, NULL, NULL),
(402198, 130202, 'اسپرينگ 12', 5, NULL, NULL),
(402268, 130202, 'ورق پلي اتيلن 10 ميل', 6, NULL, NULL),
(402317, 130202, 'پروانه 25', 5, NULL, NULL),
(402333, 130202, 'پيچ 10*7 سانتي', 5, NULL, NULL),
(402383, 130202, 'پيچ سرمته اي 5 سانتي', 5, NULL, NULL),
(402384, 130202, 'پيچ سر مته اي 4 سانتي', 5, NULL, NULL),
(402424, 130202, 'کوبلينگ 80', 5, NULL, NULL),
(402437, 130202, 'چکشي پلو وايزر (تنگستن)', 5, NULL, NULL),
(402440, 130202, 'پروانه برنزي پمپ آب', 5, NULL, NULL),
(402454, 130202, 'پيچ خودکار سر مته اي', 5, NULL, NULL),
(402504, 130202, 'پروانه 132', 5, NULL, NULL),
(402536, 130202, 'تسمه SPC4750', 5, NULL, NULL),
(402550, 130202, 'پيچ سر خزينه 12*4 سانتي', 5, NULL, NULL),
(402608, 130202, 'بلبرينگ 6204 لبه دار', 5, NULL, NULL),
(402626, 130202, 'تسمه AX52', 5, NULL, NULL),
(402629, 130202, 'پيچ آلن 12*3 سانتي', 5, NULL, NULL),
(402645, 130202, 'پيچ متري 6', 8, NULL, NULL),
(402658, 130202, 'پيچ استيل 6 * 1 سانتي', 5, NULL, NULL),
(402662, 130202, 'پيچ متري استيل 10', 8, NULL, NULL),
(402676, 130202, 'پيچ سر خزينه 6 *2 سانتي', 5, NULL, NULL),
(402677, 130202, 'بلبرينگ 11949/10', 5, NULL, NULL),
(402678, 130202, 'بلبرينگ 44649/10', 5, NULL, NULL),
(402746, 130202, 'گيج روغني 100 بار', 5, NULL, NULL),
(402816, 130202, 'بلبرينگ NU 207', 5, NULL, NULL),
(402817, 130202, 'چگنت H 207', 5, NULL, NULL),
(402818, 130202, 'درپوش عقب 132', 5, NULL, NULL),
(402819, 130202, 'پروانه 14', 5, NULL, NULL),
(402820, 130202, 'مهره 38', 5, NULL, NULL),
(402821, 130202, 'کاسه نمد 7-62-45', 5, NULL, NULL),
(402822, 130202, 'ياتاقان SNL519', 5, NULL, NULL),
(402823, 130202, 'کاسنمد TsN519', 5, NULL, NULL),
(402824, 130202, 'رينگي 170*12/5', 5, NULL, NULL),
(402825, 130202, 'کاسه نمد 50*65', 5, NULL, NULL),
(402826, 130202, 'پيچ 4 * 1 سانتي', 5, NULL, NULL),
(402827, 130202, 'کاسه نمد 12*130*110', 5, NULL, NULL),
(402828, 130202, 'پيچ و مهره 30 - 7 سانتي', 5, NULL, NULL),
(402829, 130202, 'ياتاقان FL205', 5, NULL, NULL),
(402830, 130202, 'پيچ استيل 30 -10 سانتي', 5, NULL, NULL),
(402831, 130202, 'مهره استيل 30', 5, NULL, NULL),
(402832, 130202, 'براکت جلو 132', 5, NULL, NULL),
(402833, 130202, 'اورينگ 5*270 وايتون', 5, NULL, NULL),
(402834, 130202, 'واشر لرزه گير 100', 5, NULL, NULL),
(402835, 130202, 'واشر لرزه گير 110', 5, NULL, NULL),
(402836, 130202, 'واشر لرزه گير 90', 5, NULL, NULL),
(402837, 130202, 'واشر لرزه گير 32', 5, NULL, NULL),
(402838, 130202, 'واشر لرزه گير 35', 5, NULL, NULL),
(402839, 130202, 'واشر لرزه گير 40', 5, NULL, NULL),
(402840, 130202, 'واشر لرزه گير 47', 5, NULL, NULL),
(402841, 130202, 'واشر لرزه گير 52', 5, NULL, NULL),
(402842, 130202, 'واشر لرزه گير 62', 5, NULL, NULL),
(402843, 130202, 'واشر لرزه گير 72', 5, NULL, NULL),
(402844, 130202, 'واشر لرزه گير 80', 5, NULL, NULL),
(402845, 130202, 'واشر لرزه گير 85', 5, NULL, NULL),
(402846, 130202, 'واشر لرزه گير 42', 5, NULL, NULL),
(402847, 130202, 'بلبرينگ 22313 E1', 5, NULL, NULL),
(402848, 130202, 'بلبرينگ 2313NU', 5, NULL, NULL),
(402849, 130202, 'کاسه نمد 76*57', 5, NULL, NULL),
(402850, 130202, 'پکينگ روغن ريز 6/5*75*92', 5, NULL, NULL),
(402851, 130202, 'پره آسياب آزمايشگاهي', 5, NULL, NULL),
(402852, 130202, 'واشر تخت 4', 5, NULL, NULL),
(402853, 130202, 'بلبرينگ 1211K', 5, NULL, NULL),
(402854, 130202, 'کاسه نمد ياتاقان 513', 2, NULL, NULL),
(402855, 130202, 'پولي 12/5', 5, NULL, NULL),
(402856, 130202, 'بلبرينگ NU308', 5, NULL, NULL),
(402857, 130202, 'بلبرينگ NU2211', 5, NULL, NULL),
(402858, 130202, 'خار شانه اي 20*20', 8, NULL, NULL),
(403002, 130202, 'پلاستيک زباله متوسط', 6, NULL, NULL),
(403004, 130202, 'کلاه قايقي', 5, NULL, NULL),
(403011, 130202, 'مايع ضدعفوني', 7, NULL, NULL),
(403018, 130202, 'قفل اويز 60 پارس', 5, NULL, NULL),
(403022, 130202, 'دستمال حوله اي', 5, NULL, NULL),
(403029, 130103, 'کفش ايمني مهندسي', 5, NULL, NULL),
(403043, 130103, 'مايع فوم اوه', 5, NULL, NULL),
(403047, 130103, 'نوار خطر', 6, NULL, NULL),
(403049, 130103, 'ماسک سوپاپ دار', 5, NULL, NULL),
(403067, 130103, 'کلاه يکبارمصرف', 5, NULL, NULL),
(403070, 130103, 'سم دلتاميترين', 7, NULL, NULL),
(403085, 130103, 'نانوسيل 5 ليتري', 5, NULL, NULL),
(403086, 130103, 'بنزالکونيوم کلرايد20/ 4 ليتري', 5, NULL, NULL),
(403087, 130103, 'بنزالکونيوم کلرايد 20/ -5 ليتري', 5, NULL, NULL),
(403088, 130103, 'مايع ضد عفوني با پمپ', 5, NULL, NULL),
(404001, 130103, 'باتري قلمي', 5, NULL, NULL),
(404002, 130103, 'باتري نيم قلم', 5, NULL, NULL),
(404005, 130103, 'کاتريج 712', 5, NULL, NULL),
(404007, 130103, 'کاتريج  12A', 5, NULL, NULL),
(404008, 130103, 'نمکدان', 5, NULL, NULL),
(404021, 130103, 'پرچم', 5, NULL, NULL),
(404045, 130103, 'خودکار', 5, NULL, NULL),
(404048, 130103, 'متر 3M فيسکو', 5, NULL, NULL),
(404050, 130103, 'دفتر يادداشت پاپکو', 5, NULL, NULL),
(404063, 130103, 'کاتريج 85A', 5, NULL, NULL),
(404066, 130103, 'ميخ فولادي', 5, NULL, NULL),
(404076, 130103, 'ميز کامپيوتر', 5, NULL, NULL),
(404077, 130103, 'صندلي چرخ دار', 5, NULL, NULL),
(404082, 130103, 'پيراهن', 5, NULL, NULL),
(404084, 130103, 'پوشال کولر7000', 2, NULL, NULL),
(404093, 130103, 'پارچ آبخوري', 5, NULL, NULL),
(404096, 130103, 'قفل مهماتي بزرگ', 5, NULL, NULL),
(404105, 130103, 'کاتريچ پاناسونيک', 5, NULL, NULL),
(404111, 130103, 'نايلون 100*50', 6, NULL, NULL),
(404112, 130103, 'کاردک 14 سانتي', 5, NULL, NULL),
(404116, 130103, 'سويا خوراکي', 5, NULL, NULL),
(404129, 130103, 'تينر فوري', 7, NULL, NULL),
(404134, 130103, 'ساعت ديواري', 5, NULL, NULL),
(404136, 130103, 'ماژيک علامت گذار اسنومن', 5, NULL, NULL),
(404141, 130103, 'کارتريچ 35 HP', 5, NULL, NULL),
(404144, 130103, 'کارتريج 725', 5, NULL, NULL),
(404150, 130103, 'جاروخاک بردار', 5, NULL, NULL),
(404151, 130103, 'زير کيسي', 5, NULL, NULL),
(404161, 130103, 'شيشه شو چند منظوره', 5, NULL, NULL),
(404164, 130103, 'ماشين حساب کاسيو 240-DJ', 5, NULL, NULL),
(404165, 130103, 'کاپشن شلوار', 2, NULL, NULL),
(404170, 130103, 'چسب کاغذي', 5, NULL, NULL),
(404173, 130103, 'طلق صحافي', 5, NULL, NULL),
(404179, 130103, 'دستکش لاستيکي گيلان', 5, NULL, NULL),
(404186, 130103, 'ماسک کشدار', 5, NULL, NULL),
(404190, 130103, 'روپوش سفيد', 5, NULL, NULL),
(404198, 130103, 'کاور سي دي', 5, NULL, NULL),
(404203, 130103, 'تلفن S520', 5, NULL, NULL),
(404212, 130103, 'منگنه زن ماکس', 3, NULL, NULL),
(404219, 130103, 'وايت برد 90*120', 5, NULL, NULL),
(404234, 130103, 'کاتريج 728', 5, NULL, NULL),
(404236, 130103, 'سيني', 5, NULL, NULL),
(404245, 130103, 'خودکار پايه چسب دار', 5, NULL, NULL),
(404259, 130103, 'جا دستمال حوله اي', 5, NULL, NULL),
(404266, 130103, 'نايلون 70*55 سفيد', 6, NULL, NULL),
(404279, 130103, 'باطري نيم قلم شارژي', 5, NULL, NULL),
(404291, 130103, 'منگنه زن کوچک', 5, NULL, NULL),
(404292, 130103, 'فنر مارپيچ 14', 5, NULL, NULL),
(404297, 130103, 'فنر مارپيچ 18', 5, NULL, NULL),
(404303, 130103, 'دستمال سطوح', 5, NULL, NULL),
(404329, 130103, 'چوب لباسي ديواري', 5, NULL, NULL),
(404346, 130103, 'پانچ کانگرو 700', 5, NULL, NULL),
(404355, 130103, 'فنر مارپيچ 12', 5, NULL, NULL),
(404356, 130103, 'دسته جارو فلزي', 5, NULL, NULL),
(404377, 130103, 'کاتريج 737', 5, NULL, NULL),
(404378, 130103, 'کاور A5', 1, NULL, NULL),
(404379, 130103, 'نايلون حبابدار', 8, NULL, NULL),
(404380, 130103, 'سوزن منگنه کارتن', 1, NULL, NULL),
(404381, 130103, 'فلش 32 گيگ', 5, NULL, NULL),
(404382, 130103, 'نسکافه 20 عددي', 1, NULL, NULL),
(404383, 130103, 'ظرف آبليمو خور', 5, NULL, NULL),
(405025, 130103, 'فاز متر کوچک', 5, NULL, NULL),
(405087, 130202, 'مته 4/2', 5, NULL, NULL),
(405091, 130202, 'قفل آويز 50', 5, NULL, NULL),
(405097, 130202, 'آچار يکسر رينگ 17', 5, NULL, NULL),
(405098, 130202, 'آچار فرانسه 12', 5, NULL, NULL),
(405104, 130202, 'روغندان 500 CC', 5, NULL, NULL),
(405105, 130202, 'استامبولي سفيد کاري', 5, NULL, NULL),
(405106, 130202, 'کمچه', 5, NULL, NULL),
(405129, 130202, 'متر 3 متري', 5, NULL, NULL),
(405130, 130202, 'متر 5 متري', 5, NULL, NULL),
(405138, 130202, 'کاردک تيغ کوچک', 5, NULL, NULL),
(405139, 130202, 'کاردک تيغ بزرگ', 5, NULL, NULL),
(405144, 130202, 'گردبر 32', 5, NULL, NULL),
(405159, 130202, 'مته استيل 6', 5, NULL, NULL),
(405180, 130202, 'بکس 3/8', 5, NULL, NULL),
(405188, 130202, 'دستگيره پنجره', 5, NULL, NULL),
(405282, 130202, 'مته 2/8', 5, NULL, NULL),
(405302, 130202, 'پمپ چسب آکواريوم', 5, NULL, NULL),
(405303, 130202, 'مته کاجي 5 عددي', 2, NULL, NULL),
(405304, 130202, 'پيچ اتصالاتضربه خور', 5, NULL, NULL),
(405307, 130202, 'اسفنج تميز کننده نوک هويه', 5, NULL, NULL),
(407022, 130202, 'چپقي 1/2', 5, NULL, NULL),
(407023, 130202, 'چپقي 1', 5, NULL, NULL),
(407027, 130202, 'بوشن گازي 1', 5, NULL, NULL),
(407030, 130202, 'مهره ماسوره گازي 1/2', 5, NULL, NULL),
(407031, 130202, 'زانو 1/2', 5, NULL, NULL),
(407035, 130202, 'بوشن اب 3/4', 5, NULL, NULL),
(407036, 130202, 'بوشن اب 1/2', 5, NULL, NULL),
(407038, 130202, 'توپيچ روپيچ گازي 1/2', 5, NULL, NULL),
(407039, 130202, 'مغزي 3/4', 5, NULL, NULL),
(407050, 130202, 'بوشن استيل 1/2', 5, NULL, NULL),
(407059, 130202, 'زانو گازي 1و 1/2', 5, NULL, NULL),
(407087, 130202, 'مغزي 1و1/2', 5, NULL, NULL),
(407089, 130202, 'سه راه 1/2', 5, NULL, NULL),
(407096, 130202, 'بست لوله ديواري', 5, NULL, NULL),
(407097, 130202, 'شير تک ضرب 1', 5, NULL, NULL),
(407098, 130202, 'شير تک ضرب 1/2', 5, NULL, NULL),
(407109, 130202, 'شير اطمينان1/2', 5, NULL, NULL),
(407110, 130202, 'شير اب 1/2', 5, NULL, NULL),
(407111, 130202, 'شير اب 3/4', 5, NULL, NULL),
(407155, 130202, 'ورق 8 ميل', 5, NULL, NULL),
(407171, 130202, 'تبديل 2به 1و1/2', 5, NULL, NULL),
(407194, 130202, 'شير تکضرب1/2*1', 5, NULL, NULL),
(407196, 130202, 'سه راهه 45*110', 5, NULL, NULL),
(407205, 130202, 'بست کمر بندي 4/8*25', 5, NULL, NULL),
(407217, 130202, 'شير يکطرفه 1 اينچ', 5, NULL, NULL),
(407247, 130202, 'سه راهه 3 اينچ گالوانيزه', 5, NULL, NULL),
(407251, 130202, 'زانو 1 اينچ', 5, NULL, NULL),
(407255, 130202, 'ميلگرد 10 اجدار', 6, NULL, NULL),
(407265, 130202, 'لوله 5 گازي', 8, NULL, NULL),
(407286, 130202, 'زانو برقي 40', 5, NULL, NULL),
(407410, 130202, 'لوله 1 اينچ گالوانيزه', 4, NULL, NULL),
(407423, 130202, 'دريچه کولر', 5, NULL, NULL),
(407426, 130202, 'زانو 1/2 جوشي استيل', 5, NULL, NULL),
(407428, 130202, 'شيلنگ خور 1/2', 5, NULL, NULL),
(407435, 130202, 'قوطي 3*3', 6, NULL, NULL),
(407496, 130202, 'زانو 2 اينچ 45 درجه استيل', 5, NULL, NULL),
(407502, 130202, 'ماله فلزي', 5, NULL, NULL),
(407572, 130202, 'مغزي 2 اينچ استيل', 5, NULL, NULL),
(407573, 130202, 'مهره ماسوره 2 اينچ استيل', 5, NULL, NULL),
(407574, 130202, 'درپوش 2 اينچ استيل', 5, NULL, NULL),
(407603, 130202, 'گردبر 27', 5, NULL, NULL),
(407604, 130202, 'گردبر 40', 5, NULL, NULL),
(407645, 130202, 'فشار شکن 1 اينچ', 5, NULL, NULL),
(407648, 130202, 'قفل يخچال', 5, NULL, NULL),
(407667, 130202, 'ميلگرد پلي اتيلن', 6, NULL, NULL),
(407672, 130202, 'تبديل 3/8*1/2', 5, NULL, NULL),
(407687, 130202, 'بوشن 110 پوليکا', 5, NULL, NULL),
(407730, 130202, 'قفل 2/5 سانتي', 5, NULL, NULL),
(407731, 130202, 'پيچ آلن 24*8 سانتي', 5, NULL, NULL),
(407732, 130202, 'لوله پلي اتيلن 32', 8, NULL, NULL),
(407733, 130202, 'پوساب 80', 5, NULL, NULL),
(407747, 130202, 'قوطي 3*1', 6, NULL, NULL),
(407833, 130202, 'چسب ويکتوري رينز', 5, NULL, NULL),
(407834, 130202, 'فلنج 90', 5, NULL, NULL),
(407845, 130202, 'تيغه برش پروفيل', 5, NULL, NULL),
(407856, 130202, 'جعبه 14*22*30', 5, NULL, NULL),
(407858, 130202, 'لوله گالوانيزه 1/2', 4, NULL, NULL),
(407875, 130202, 'قوطي 8*4', 6, NULL, NULL),
(407883, 130202, 'مغزي شير مخلوط اهرمي', 5, NULL, NULL),
(407886, 130202, 'شير فشاري آبسردکن', 5, NULL, NULL),
(407973, 130202, 'وارنيش 16', 8, NULL, NULL),
(407991, 130202, 'جعبه تقسيم 9*10', 5, NULL, NULL),
(407994, 130202, 'چرخ سطل زباله', 5, NULL, NULL),
(408030, 130202, 'اورينگ 3-59', 5, NULL, NULL),
(408036, 130202, 'اورينگ 5-265', 5, NULL, NULL),
(409008, 130202, 'لاستيک گاري', 5, NULL, NULL),
(409019, 130202, 'روغن هيدروليک 68', 7, NULL, NULL),
(409036, 130202, 'کنس بيروني دکل 2 تن', 5, NULL, NULL),
(409037, 130202, 'کنس دروني دکل 2 تن', 5, NULL, NULL),
(409064, 130202, 'تيوپ 9*600', 5, NULL, NULL),
(409104, 130202, 'پيچ و مهره چرخ جلو تراکتور', 5, NULL, NULL),
(409131, 130202, 'فيلتر آهنربائي', 5, NULL, NULL),
(409136, 130202, 'لوازم جک فرمان لودر', 2, NULL, NULL),
(409218, 130202, 'باطري 170 آمپر', 5, NULL, NULL),
(409318, 130202, 'کاسه نمد 13-100-75', 5, NULL, NULL),
(409320, 130202, 'واشر چاکنيت چرخ جلو 2 تن', 5, NULL, NULL),
(409323, 130202, 'واشر دکل 2 تن', 5, NULL, NULL),
(409357, 130202, 'پايه دينام ليفتراک', 5, NULL, NULL),
(409398, 130202, 'لاستيک ليفتراک برقي 9*8*21', 5, NULL, NULL),
(409438, 130202, 'سيم گاز ليفتراک', 5, NULL, NULL),
(409446, 130202, 'واشر ساده دکل 2 تن', 5, NULL, NULL),
(409483, 130202, 'رولبرينگ دکل T2', 5, NULL, NULL),
(409484, 130202, 'رولينگ ريل دکل', 5, NULL, NULL),
(409485, 130202, 'لوازم جک بالابر ليفتراک', 2, NULL, NULL),
(409486, 130202, 'لاستيک ليفتراک 10*50*6', 5, NULL, NULL),
(409515, 130202, 'کنس بيروني دکل 3T اصلي', 5, NULL, NULL),
(409516, 130202, 'کنس دروني دکل 3Tاصلي', 5, NULL, NULL),
(409517, 130202, 'رولبرينگ دکل اصلي SKF', 5, NULL, NULL),
(409518, 130202, 'واشر خاردار دکل اصلي', 5, NULL, NULL),
(409519, 130202, 'واشر ساده دکل اصلي', 5, NULL, NULL),
(409524, 130202, 'لوازم پمپ اينچينگ', 2, NULL, NULL),
(409542, 130202, 'توپي کامل ليفتراک 2 تن با دنده ستاره اي', 5, NULL, NULL),
(409543, 130202, 'گلدوني چرخ جلو 2 تن', 5, NULL, NULL),
(409544, 130202, 'اورينگ چرخ جلو 2 تن', 5, NULL, NULL),
(409545, 130202, 'واشر نسوز سر پلوس', 5, NULL, NULL),
(409546, 130202, 'پولي دينام لودر', 5, NULL, NULL),
(409547, 130202, 'تو باکي گازوئيل ليفتراک', 5, NULL, NULL),
(409548, 130202, 'واسطه واتر پمپ لودر', 5, NULL, NULL),
(409549, 130202, 'واشر دريچه واتر پمپ', 5, NULL, NULL),
(409550, 130202, 'لاستيک داخل تانک لودر', 5, NULL, NULL),
(409551, 130202, 'واشر سر و ته تانک لودر', 5, NULL, NULL),
(409552, 130202, 'قرقري ليور تانک لودر', 5, NULL, NULL),
(409553, 130202, 'شيشه +لاستيک تانک لودر', 5, NULL, NULL),
(409554, 130202, 'بوش دوقلو کاسه جک 950', 2, NULL, NULL),
(409555, 130202, 'لوازم جک 7 تيکه لودر', 2, NULL, NULL),
(409556, 130202, 'کيت تانک هيدروليک لودر', 2, NULL, NULL),
(409557, 130202, 'پين جک فرمان لودر950', 5, NULL, NULL),
(409558, 130202, 'بوش جک فرمان لودر 950', 5, NULL, NULL),
(409559, 130202, 'گريس شاسي پارس', 6, NULL, NULL),
(409560, 130202, 'روغن GS4', 7, NULL, NULL),
(409561, 130202, 'فيوز 80V DC 100A', 5, NULL, NULL),
(410009, 130202, 'لامپ مهتابي 40W', 5, NULL, NULL),
(410015, 130202, 'مادگي سه فاز', 5, NULL, NULL),
(410016, 130202, 'نري سه فاز', 5, NULL, NULL),
(410017, 130202, 'مادگي تک فاز', 5, NULL, NULL),
(410024, 130202, 'فيوز تک فاز 10آمپر', 5, NULL, NULL),
(410031, 130202, 'فيوز تک فاز 16آمپر', 5, NULL, NULL),
(410032, 130202, 'فيوز سه فاز 63آمپر', 5, NULL, NULL),
(410039, 130202, 'کليد تک پل', 5, NULL, NULL),
(410096, 130202, 'پريز روکارتکفاز', 5, NULL, NULL),
(410108, 130202, 'ميکروسوئيچ', 5, NULL, NULL),
(410112, 130202, 'سنسور', 5, NULL, NULL),
(410129, 130202, 'کابل 2*2/5', 8, NULL, NULL),
(410145, 130202, 'استارت مهتابي', 5, NULL, NULL),
(410155, 130202, 'کليد گردان 3 فاز', 5, NULL, NULL),
(410170, 130202, 'موتور 3/4 کولر', 5, NULL, NULL),
(410172, 130202, 'ترمينال', 5, NULL, NULL),
(410207, 130202, 'کابل افشان 2/5*4', 8, NULL, NULL),
(410237, 130202, 'چهارراه برق', 5, NULL, NULL),
(410239, 130202, 'دو شاخه پلاستيکي', 5, NULL, NULL),
(410247, 130202, 'مجموعه نرم افزار گردو', 1, NULL, NULL),
(410250, 130202, 'کليد استارت تکي', 5, NULL, NULL),
(410254, 130202, 'ترانس 400W', 5, NULL, NULL),
(410272, 130202, 'تيغه کمکي کنتاکتور', 5, NULL, NULL),
(410273, 130202, 'فيوز حرارتي 2/5-1/6', 5, NULL, NULL),
(410305, 130202, 'آداپتور', 5, NULL, NULL),
(410324, 130202, 'رله فيندر', 5, NULL, NULL),
(410332, 130202, 'لامپ خياري 400 وات', 5, NULL, NULL),
(410351, 130202, 'تابلو فلزي', 5, NULL, NULL),
(410354, 130202, 'شير برقي تک بوبين 5/2-1/4', 5, NULL, NULL),
(410359, 130202, 'شاسي تله دوبل', 5, NULL, NULL),
(410360, 130202, 'جعبه شاسي تله 1 دکمه', 5, NULL, NULL),
(410389, 130202, 'کليد فرمان تله دو طرفه سويچدار', 5, NULL, NULL),
(410395, 130202, 'خازن 30', 5, NULL, NULL),
(410416, 130202, 'کابل 1/5*2', 8, NULL, NULL),
(410423, 130202, 'ابميوه', 5, NULL, NULL),
(410424, 130202, 'روغن لادن', 7, NULL, NULL),
(410425, 130202, 'نسکافه', 5, NULL, NULL),
(410428, 130202, 'اب معدني 1/2', 1, NULL, NULL),
(410453, 130202, 'گريس EP3', 6, NULL, NULL),
(410470, 130202, 'چسب سراجي', 6, NULL, NULL),
(410486, 130202, 'ليبل 40*40', 5, NULL, NULL),
(410487, 130202, 'ريبون 300*40', 5, NULL, NULL),
(410488, 130202, 'سندان آسياب', 5, NULL, NULL),
(411003, 130202, 'لولا 14', 5, NULL, NULL),
(411014, 130202, 'تيغه ساب ميني فرز', 5, NULL, NULL),
(411017, 130202, 'لولا 18', 5, NULL, NULL),
(411021, 130202, 'تيغه برش 1/6 ميل', 5, NULL, NULL),
(411044, 130202, 'سيم نقره', 5, NULL, NULL),
(411046, 130202, 'سيم جوش 7018 -2/5', 6, NULL, NULL),
(411079, 130202, 'تسمه 4 استيل', 4, NULL, NULL),
(412005, 130202, 'کانکتور 8و1/4', 5, NULL, NULL),
(412008, 130202, 'کانکتور 8و1/2', 5, NULL, NULL),
(412035, 130202, 'نخ نسوز 10', 8, NULL, NULL),
(412070, 130202, 'شيلنگ آب کولر', 8, NULL, NULL),
(412071, 130202, 'شير تخليه کولر', 5, NULL, NULL),
(412072, 130202, 'شناور کولر', 5, NULL, NULL),
(412073, 130202, 'پمپ آب کولر', 5, NULL, NULL),
(412088, 130202, 'گردبر 22', 5, NULL, NULL),
(412091, 130202, 'چراغ سيگنال تله LED قرمز', 5, NULL, NULL),
(412101, 130202, 'چراغ گردون آژير دار220V', 5, NULL, NULL),
(412108, 130202, 'کانکتور 3/8*8', 5, NULL, NULL),
(412116, 130202, 'گيج روغني 16-0 بار', 5, NULL, NULL),
(412136, 130202, 'شيلنگ باد 6', 8, NULL, NULL),
(412140, 130202, 'کانکتور 1/2 *10', 5, NULL, NULL),
(412179, 130202, 'فيلتر روغن کمپرسور باد WD13145', 5, NULL, NULL),
(412180, 130202, 'فيلتر هوا کمپرسوربادC20325/2', 5, NULL, NULL),
(412184, 130202, 'فيلتر هوا ADF کمپرسور باد', 5, NULL, NULL),
(412193, 130202, 'شير يکطرفه 1/8', 5, NULL, NULL),
(412196, 130202, 'شير 90 درجه 1/2-1 ترک 220V', 5, NULL, NULL),
(412197, 130202, 'رابط 10-10', 5, NULL, NULL),
(412198, 130202, 'رابط 10-8', 5, NULL, NULL),
(412199, 130202, 'رابط 8 - 8', 5, NULL, NULL),
(412200, 130202, 'فيلتر دراير 3/8', 5, NULL, NULL),
(412201, 130202, 'فيلتر هوا بيروني کماتسوPC220/6', 5, NULL, NULL),
(412202, 130202, 'فيلتر هوا دروني کماتسوPc220/6', 5, NULL, NULL),
(412203, 130202, 'فيلتر سپريتور لب بريدهSH/HY90/Hy55', 5, NULL, NULL),
(412204, 130202, 'شير 90درجه 1 اينچ 220 ولت', 5, NULL, NULL),
(413002, 130202, 'رنگ مشکي', 6, NULL, NULL),
(413003, 130202, 'رنگ زرد گل ماشي', 6, NULL, NULL),
(413010, 130202, 'قلم 2', 5, NULL, NULL),
(413016, 130202, 'ضد زنگ نارنجي', 6, NULL, NULL),
(413019, 130202, 'رنگ قرمز توپ', 6, NULL, NULL),
(413025, 130202, 'بتونه فوري', 6, NULL, NULL),
(413027, 130202, 'رنگ بردار', 5, NULL, NULL),
(413029, 130202, 'رنگ آبي', 6, NULL, NULL),
(413030, 130202, 'رنگ سبز', 6, NULL, NULL),
(413031, 130202, 'رنگ پودري زرد', 6, NULL, NULL),
(413035, 130202, 'رنگ زرد ليمويي', 6, NULL, NULL),
(413043, 130202, 'ضد زنگ نارنجي توپ', 6, NULL, NULL),
(413046, 130202, 'رنگ خشک کن', 5, NULL, NULL),
(413057, 130202, 'رنگ کرم 21', 6, NULL, NULL),
(413058, 130202, 'رنگ زرد 21', 6, NULL, NULL),
(413059, 130202, 'رنگ سفيد 21', 6, NULL, NULL),
(413060, 130202, 'رنگ آبي 21', 6, NULL, NULL),
(414008, 130202, 'نازل پلاستيکي 1/2', 5, NULL, NULL),
(414009, 130202, 'زانو 2 اينچ استيل', 5, NULL, NULL),
(414010, 130202, 'شير تکضرب 2 اينچ استيل', 5, NULL, NULL),
(414011, 130202, 'زانو 1 اينچ استيل', 5, NULL, NULL),
(414012, 130202, 'مغزي 1 اينچ استيل', 5, NULL, NULL),
(414013, 130202, 'مهره ماسوره 1 اينچ استيل', 5, NULL, NULL),
(414014, 130202, 'شير تکضرب 1 اينچ استيل', 5, NULL, NULL),
(414015, 130202, 'تبديل 3/4*1 اينچ استيل', 5, NULL, NULL),
(414016, 130202, 'تبديل 1/2*1 اينچ استيل', 5, NULL, NULL),
(414017, 130202, 'جا دستمال توالت', 5, NULL, NULL),
(414018, 130202, 'سه راهه پلي اتيلن 32', 5, NULL, NULL),
(414019, 130202, 'زانو پلي اتيلن 1*32', 5, NULL, NULL),
(414020, 130202, 'بوشن 16', 5, NULL, NULL),
(414021, 130202, 'پوشال کولر 5000', 2, NULL, NULL),
(414022, 130202, 'پيست I500 CC', 5, NULL, NULL),
(414023, 130202, 'خمير سيليکون 304', 5, NULL, NULL),
(414024, 130202, 'محلول رسوب زدايي ديگ بخار', 6, NULL, NULL),
(414025, 130202, 'چرخ دنده ميکسر بنايي', 5, NULL, NULL),
(414026, 130202, 'لوله 6 اينچ گازي', 4, NULL, NULL),
(414027, 130202, 'گاري بنايي', 5, NULL, NULL),
(414028, 130202, 'ريل 3 تکه 60 سانتي کابينت', 5, NULL, NULL),
(414029, 130202, 'تبديل 1/2*1 به1/2', 5, NULL, NULL),
(414030, 130202, 'لوله 3 اينچ گالوانيزه', 4, NULL, NULL),
(414031, 130202, 'فلوتر تمام استيل ديگ بخار', 5, NULL, NULL),
(414032, 130202, 'پلاتين ميکسر بنايي', 5, NULL, NULL),
(414033, 130202, 'لوله پوليکا 40', 8, NULL, NULL),
(414034, 130202, 'فوم 40', 8, NULL, NULL),
(414035, 130202, 'چسب ماستيک 22', 6, NULL, NULL),
(414036, 130202, 'قالب 75 لوله سبز', 5, NULL, NULL),
(414037, 130202, 'هرز گرد 1 اينچ', 5, NULL, NULL),
(420001, 130202, 'کيت سختي کل', 5, NULL, NULL),
(420029, 130202, 'کاردک', 5, NULL, NULL),
(420030, 130202, 'چرخ دنده 35', 5, NULL, NULL),
(420033, 130202, 'چگنت H2316', 5, NULL, NULL),
(420038, 130202, 'چرخ دنده 18', 5, NULL, NULL),
(420049, 130202, 'فيلتر روغن لودر', 5, NULL, NULL),
(420050, 130202, 'فيلتر گازوئيل', 5, NULL, NULL),
(420051, 130202, 'فيلتر روغن ليفتراک', 5, NULL, NULL),
(420057, 130202, 'بلبرينگ 6202', 5, NULL, NULL),
(420060, 130202, 'بلبرينگ 6205', 5, NULL, NULL),
(420061, 130202, 'بلبرينگ 6206', 5, NULL, NULL),
(420062, 130202, 'بلبرينگ 6207', 5, NULL, NULL),
(420071, 130202, 'بلبرينگ 6301', 5, NULL, NULL),
(420075, 130202, 'بلبرينگ 2316', 5, NULL, NULL),
(420077, 130202, 'بلبرينگ 6314', 5, NULL, NULL),
(420080, 130202, 'بلبرينگ 6313', 5, NULL, NULL),
(420082, 130202, 'بلبرينگ 30218', 5, NULL, NULL),
(420094, 130202, 'بلبرينگ 6309', 5, NULL, NULL),
(420095, 130202, 'بلبرينگ 6308', 5, NULL, NULL),
(420096, 130202, 'بلبرينگ 6307', 5, NULL, NULL),
(420134, 130202, 'توري   75ميل', 5, NULL, NULL),
(420155, 130202, 'انبر جوشکاري', 5, NULL, NULL),
(420159, 130202, 'بست فلزي1/2', 5, NULL, NULL),
(420162, 130202, 'چسب123', 5, NULL, NULL),
(420164, 130202, 'سوئيچ استارت لودر', 5, NULL, NULL),
(420167, 130202, 'بست متري', 8, NULL, NULL),
(420175, 130202, 'سيم جوش 3', 6, NULL, NULL),
(420178, 130202, 'لوازم جک', 5, NULL, NULL),
(420181, 130202, 'روغن کمپرسور باد', 5, NULL, NULL),
(420182, 130202, 'اسپري کنتاکشو', 5, NULL, NULL),
(420193, 130202, 'کاسه نمد 35627', 5, NULL, NULL),
(420237, 130202, 'تسمه A96', 5, NULL, NULL),
(420246, 130202, 'تسمه A60', 5, NULL, NULL),
(420247, 130202, 'تسمه A62', 5, NULL, NULL),
(420275, 130202, 'ياتاقان F208', 5, NULL, NULL),
(420289, 130202, 'توري 1ميل', 5, NULL, NULL),
(420300, 130202, 'هزار خاري', 5, NULL, NULL),
(420316, 130202, 'سيم بکسل 10', 8, NULL, NULL),
(420329, 130202, 'دستگيره', 5, NULL, NULL),
(420331, 130202, 'قفل زنجير 60', 5, NULL, NULL),
(420335, 130202, 'چکشي آسياب 6*60*175', 5, NULL, NULL),
(420342, 130202, 'تيغه برش 1ميل', 5, NULL, NULL),
(420343, 130202, 'تيغه برش 3ميل', 5, NULL, NULL),
(420346, 130202, 'مهره 12', 5, NULL, NULL),
(420348, 130202, 'خار 12', 8, NULL, NULL),
(420349, 130202, 'شيلنگ 1/2', 8, NULL, NULL),
(420364, 130202, 'شمع', 5, NULL, NULL),
(420372, 130202, 'لولا 20', 5, NULL, NULL),
(420374, 130202, 'فيلتر هوا ليفتراک', 5, NULL, NULL),
(420386, 130202, 'لوازم سيلندر 40', 5, NULL, NULL),
(420387, 130202, 'لوازم سيلندر 32', 5, NULL, NULL),
(420401, 130202, 'فيلتر گيربکس لودر', 5, NULL, NULL),
(420410, 130202, 'فيلتر تانک هيدروليک لودر 950', 5, NULL, NULL),
(420427, 130202, 'سوپاپ پمپ کارواش کامل', 5, NULL, NULL),
(420432, 130202, 'تبديل 3 اينچ به 2 اينچ', 5, NULL, NULL),
(420466, 130202, 'تسمه CX-57', 5, NULL, NULL),
(420469, 130202, 'آچار يکسر رينگ 19', 5, NULL, NULL),
(420471, 130202, 'گريس نسوز SKf', 5, NULL, NULL),
(420478, 130202, 'گريس خور 10', 5, NULL, NULL),
(420481, 130202, 'دنده سر پلوس ليفتراک', 5, NULL, NULL),
(420511, 130202, 'فيلترسپراتور کمپرسور باد', 5, NULL, NULL),
(420512, 130202, 'حلال جت پرينتر', 7, NULL, NULL),
(420513, 130202, 'جوهر جت پرينتر', 7, NULL, NULL),
(420548, 130202, 'پولي 1 شياره 100', 5, NULL, NULL),
(420564, 130202, 'فيلتر روغن تراکتور', 5, NULL, NULL),
(420587, 130202, 'پولي 7/5', 5, NULL, NULL),
(420617, 130202, 'دري واسطه G2  اسب 2', 5, NULL, NULL),
(420619, 130202, 'پروانه 100', 5, NULL, NULL),
(420627, 130202, 'بلبرينگ 6005', 5, NULL, NULL),
(420634, 130202, 'پارچه نسوز', 8, NULL, NULL),
(420638, 130202, 'پيچ 8*2', 5, NULL, NULL),
(420639, 130202, 'رولپلاک', 5, NULL, NULL),
(420640, 130202, 'رولپلاک 8*2', 5, NULL, NULL),
(420641, 130202, 'چرخ ثابت 7/5', 5, NULL, NULL),
(420653, 130202, 'تسمه D118', 5, NULL, NULL),
(420685, 130202, 'فنر', 5, NULL, NULL),
(420688, 130202, 'واشر سيم دار 2 ميل 1 اينچ', 5, NULL, NULL),
(420707, 130202, 'چرخ ثابت 28', 5, NULL, NULL),
(420709, 130202, 'فيبر وفنر', 5, NULL, NULL),
(420756, 130202, 'پيچ متري 10', 5, NULL, NULL),
(420757, 130202, 'پيچ متري 12', 5, NULL, NULL),
(420780, 130202, 'واتر پمپ ليفتراک', 5, NULL, NULL),
(420787, 130202, 'مهره چگنيت توپي چرخ ليفتراک', 5, NULL, NULL),
(420799, 130202, 'ورق پلي اورتان 10 ميل', 6, NULL, NULL),
(420812, 130202, 'ورق استيل 2 ميل', 6, NULL, NULL),
(420820, 130202, 'مهره 36', 5, NULL, NULL),
(420822, 130202, 'نخ نسوز', 6, NULL, NULL),
(420833, 130202, 'بست کمربندي 4/8*30', 5, NULL, NULL),
(420842, 130202, 'سيم لحيم', 1, NULL, NULL),
(420845, 130202, 'مته 11', 5, NULL, NULL),
(420849, 130202, 'پيچ آلن سر تخت 30*10', 5, NULL, NULL),
(430005, 130202, 'مته 5', 5, NULL, NULL),
(430011, 130202, 'سوزن چرخ دوخت بزرگ', 5, NULL, NULL),
(430012, 130202, 'شيشه ماسک سياه', 5, NULL, NULL),
(430016, 130202, 'عينک برشکاري', 5, NULL, NULL),
(430017, 130202, 'حشره کش', 5, NULL, NULL),
(430022, 130202, 'مغزي 1/2', 5, NULL, NULL),
(430025, 130202, 'تيغه کمان اره', 5, NULL, NULL),
(430027, 130202, 'شير 1/2', 5, NULL, NULL),
(430031, 130202, 'چسب آکواريوم', 5, NULL, NULL),
(430032, 130202, 'چسب برق', 5, NULL, NULL),
(430033, 130202, 'چسب شيشه 5 سانتي', 5, NULL, NULL),
(430034, 130202, 'چسب شيشه اي 1 سانتي', 5, NULL, NULL),
(430037, 130202, 'بست پايه دار 1/2', 5, NULL, NULL),
(430042, 130202, 'ضد يخ', 5, NULL, NULL),
(430043, 130202, 'مته  6', 5, NULL, NULL),
(430044, 130202, 'مته 4', 5, NULL, NULL),
(430045, 130202, 'مته 7', 5, NULL, NULL),
(430049, 130202, 'قفل کتابي', 5, NULL, NULL),
(430054, 130202, 'آب رادياتور', 7, NULL, NULL),
(430058, 130202, 'شير تکضرب 2اينچ', 5, NULL, NULL),
(430059, 130202, 'شير تکضرب 3/4', 5, NULL, NULL),
(430065, 130202, 'نبشي 6', 8, NULL, NULL),
(430066, 130202, 'ورق 5MM', 8, NULL, NULL),
(430070, 130202, 'شيلنگ تراز', 8, NULL, NULL),
(430075, 130202, 'تبديل 1به 1  1/4', 5, NULL, NULL),
(430077, 130202, 'اسپري فوم درزگير', 5, NULL, NULL),
(430078, 130202, 'تفلون', 5, NULL, NULL),
(430079, 130202, 'چسب مزدا', 5, NULL, NULL),
(430080, 130202, 'رول ولت 10*100', 5, NULL, NULL),
(430092, 130202, 'لوله3/4', 8, NULL, NULL),
(430095, 130202, 'مغزي 2 اينچ', 5, NULL, NULL),
(430096, 130202, 'شيلنگ باد', 8, NULL, NULL),
(430099, 130202, 'شيلنگ باد شماره 8', 8, NULL, NULL),
(430105, 130202, 'اسپري رنگ', 5, NULL, NULL),
(430106, 130202, 'ژل پاک کننده استيل', 5, NULL, NULL),
(430126, 130202, 'لوله گالوانيزه 16', 5, NULL, NULL),
(430132, 130202, 'الم شير مخلوط', 5, NULL, NULL),
(430133, 130202, 'لوله پلي اتيلن 63', 8, NULL, NULL),
(430138, 130202, 'قوطي 2*2', 6, NULL, NULL),
(430139, 130202, 'نبشي 3', 6, NULL, NULL),
(430140, 130202, 'نبشي 4', 6, NULL, NULL),
(430143, 130202, 'ورق 3 آجدار', 6, NULL, NULL),
(430144, 130202, 'ورق 3 سياه', 6, NULL, NULL),
(430153, 130202, 'تير آهن14', 8, NULL, NULL),
(430155, 130202, 'دسته کلنگ', 5, NULL, NULL),
(430156, 130202, 'دسته بيل', 5, NULL, NULL),
(430159, 130202, 'لوله110', 8, NULL, NULL),
(430172, 130202, 'قاشقک بالابر 16', 5, NULL, NULL),
(430191, 130202, 'شيشه ماسک سفيد', 5, NULL, NULL),
(430192, 130202, 'بست باطري', 5, NULL, NULL),
(430194, 130202, 'شيلنگ کارواش', 8, NULL, NULL),
(430198, 130202, 'نايلون عريض', 6, NULL, NULL),
(430204, 130202, 'زانو 45درجه 110', 5, NULL, NULL),
(430211, 130202, 'سنگ برش بزرگ', 5, NULL, NULL),
(430212, 130202, 'سنگ ساب بزرگ', 5, NULL, NULL),
(430244, 130202, 'قلم 1 اينچ', 5, NULL, NULL),
(430251, 130202, 'ورق 6 ميل', 6, NULL, NULL),
(430254, 130202, 'ورق 2 ميل', 6, NULL, NULL),
(430263, 130202, 'فوم کراسينگ', 5, NULL, NULL),
(430267, 130202, 'کاسه بروس بافته', 5, NULL, NULL),
(430269, 130202, 'کاسه بروس نرم کوچک', 5, NULL, NULL),
(430271, 130202, 'مغزي 1', 5, NULL, NULL),
(430273, 130202, 'لوله 3', 4, NULL, NULL),
(430279, 130202, 'پوساب نرم', 5, NULL, NULL),
(430304, 130202, 'چسب سنگ', 6, NULL, NULL),
(430306, 130202, 'ضدزنگ طوسي', 6, NULL, NULL),
(440001, 130202, 'خودکار آبي', 5, NULL, NULL),
(440003, 130202, 'خودکار قرمز', 5, NULL, NULL),
(440007, 130202, 'زونکنA5', 5, NULL, NULL),
(440010, 130202, 'ريبون', 5, NULL, NULL),
(440013, 130202, 'پوشه', 5, NULL, NULL),
(440014, 130202, 'ماژيک وايت برد', 5, NULL, NULL),
(440015, 130202, 'ماژيک فسفري', 5, NULL, NULL),
(440016, 130202, 'لاک غلط گير', 5, NULL, NULL),
(440018, 130202, 'سوزن ته گرد', 5, NULL, NULL),
(440019, 130202, 'سوزن منگنه بزرگ', 5, NULL, NULL),
(440021, 130202, 'پاکت نامه', 5, NULL, NULL),
(440032, 130202, 'سيمان سفيد', 6, NULL, NULL),
(440034, 130202, 'مته 3  5', 5, NULL, NULL),
(440036, 130202, 'گلند PG11', 5, NULL, NULL),
(440038, 130202, 'گلندPG16', 5, NULL, NULL),
(440039, 130202, 'گلندPG13  5', 5, NULL, NULL),
(440041, 130202, 'ماسک پارچه اي', 5, NULL, NULL),
(440042, 130202, 'تايد', 5, NULL, NULL),
(440043, 130202, 'قند', 6, NULL, NULL),
(440044, 130202, 'دستمال کاغذي', 5, NULL, NULL),
(440049, 130202, 'اسکاج', 5, NULL, NULL),
(440050, 130202, 'مايع ظرفشويي', 5, NULL, NULL),
(440051, 130202, 'برگه A5', 5, NULL, NULL),
(440052, 130202, 'تي لاستيکي', 5, NULL, NULL),
(440053, 130202, 'تي نخي', 5, NULL, NULL),
(440054, 130202, 'تينر', 5, NULL, NULL),
(440055, 130202, 'سرتاس بزرگ', 5, NULL, NULL),
(440056, 130202, 'سرتاس کوچک', 5, NULL, NULL),
(440057, 130202, 'سرتاس متوسط', 5, NULL, NULL),
(440059, 130202, 'برگه A4', 5, NULL, NULL),
(440070, 130202, 'تبديل110-90', 5, NULL, NULL),
(440075, 130202, 'دستکش کف چرم', 5, NULL, NULL),
(440080, 130202, 'شيشه شو', 5, NULL, NULL),
(440082, 130202, 'پارچه تنظيف', 8, NULL, NULL),
(440083, 130202, 'روغن 50', 5, NULL, NULL),
(440087, 130202, 'خودکار تبليغاتي', 5, NULL, NULL),
(440090, 130202, 'پلاستيک زيپ کيپ', 5, NULL, NULL),
(440091, 130202, 'سفره يکبار مصرف', 5, NULL, NULL),
(440092, 130202, 'مايع دستشويي', 5, NULL, NULL),
(440093, 130202, 'مايع جرم گير', 5, NULL, NULL),
(440094, 130202, 'مايع لکه پاکن', 5, NULL, NULL),
(440096, 130202, 'کلاه نقابدار', 5, NULL, NULL),
(440112, 130202, 'کش پهن', 8, NULL, NULL),
(440118, 130202, 'جا مايع', 5, NULL, NULL),
(440121, 130202, 'ليوان يکبار مصرف', 5, NULL, NULL),
(440124, 130202, 'دستکش نخي', 5, NULL, NULL),
(440125, 130202, 'دستکش لاستيکي', 5, NULL, NULL),
(440128, 130202, 'پوشال کولر3500', 5, NULL, NULL),
(440132, 130202, 'کاغذ پرينتر A5', 5, NULL, NULL),
(440139, 130202, 'آب مقطر', 7, NULL, NULL),
(440141, 130202, 'پلاستيک فريزر', 6, NULL, NULL),
(440148, 130202, 'لباس يکبار مصرف', 5, NULL, NULL),
(440149, 130202, 'جا چسبي', 5, NULL, NULL),
(440152, 130202, 'استامپ', 5, NULL, NULL),
(440158, 130202, 'شکر', 6, NULL, NULL),
(440161, 130202, 'گوشي هدفني', 5, NULL, NULL),
(440162, 130202, 'تيغ موکت بري', 5, NULL, NULL),
(440170, 130202, 'مايع دستشويي اوه', 7, NULL, NULL),
(440171, 130202, 'پد طبي', 5, NULL, NULL),
(440175, 130202, 'چکمه بلند', 5, NULL, NULL),
(440176, 130202, 'کفش ايمني', 5, NULL, NULL),
(440178, 130202, 'پلاستيک زيپ کيپ کوچک', 5, NULL, NULL),
(440182, 130202, 'تخته پاک کن', 5, NULL, NULL),
(440189, 130202, 'چاي', 6, NULL, NULL),
(440191, 130202, 'لباس کار', 5, NULL, NULL),
(440198, 130202, 'آينه محدب', 5, NULL, NULL),
(440199, 130202, 'فندک گاز', 5, NULL, NULL),
(440203, 130202, 'جارو کار واشي', 5, NULL, NULL),
(440205, 130202, 'چسب موش', 5, NULL, NULL),
(440209, 130202, 'سم دورسبان', 7, NULL, NULL),
(440210, 130202, 'سطل پدالي', 5, NULL, NULL),
(440214, 130202, 'اسپري خوشبو کننده', 5, NULL, NULL),
(440216, 130202, 'مواد ضدعفوني', 6, NULL, NULL),
(440217, 130202, 'جارو فراشي', 6, NULL, NULL),
(440235, 130202, 'ماکاروني', 1, NULL, NULL),
(440236, 130202, 'رب گوجه', 5, NULL, NULL),
(440252, 130202, 'خلال', 5, NULL, NULL),
(440256, 130202, 'شکلات', 5, NULL, NULL),
(440257, 130202, 'بيسکويت', 5, NULL, NULL),
(440259, 130202, 'شيشه فلاکس', 5, NULL, NULL),
(440263, 130202, 'نايلون 80*65', 6, NULL, NULL),
(440266, 130202, 'نايلون 35*25', 6, NULL, NULL),
(440269, 130202, 'زير پائي', 5, NULL, NULL),
(440278, 130202, 'ليبل 75*40', 5, NULL, NULL),
(440281, 130202, 'خرما', 6, NULL, NULL),
(440283, 130202, 'ليوان يکبار مصرف کاغذي', 5, NULL, NULL),
(440287, 130202, 'کيک', 5, NULL, NULL),
(440294, 130202, 'نايلون 90*70', 6, NULL, NULL),
(440296, 130202, 'کاور A4', 1, NULL, NULL),
(440301, 130202, 'جوهر استامپ', 5, NULL, NULL),
(440317, 130202, 'دفتر تلفن', 5, NULL, NULL),
(440322, 130202, 'کفش مردانه', 5, NULL, NULL),
(440328, 130202, 'ماژيک معمولي', 5, NULL, NULL),
(440334, 130202, 'جازونکني 5 طبقه P  V  C', 5, NULL, NULL),
(440336, 130202, 'دستکش تمام چرم', 5, NULL, NULL),
(440337, 130202, 'دستکش کف مواد', 5, NULL, NULL),
(440341, 130202, 'طناب', 8, NULL, NULL),
(440353, 130202, 'بتونه سنگي', 5, NULL, NULL),
(440357, 130202, 'محافظ غذا', 5, NULL, NULL),
(440370, 130202, 'قفل مغزي دربازکن', 5, NULL, NULL),
(440379, 130202, 'سفره پلاستيکي', 8, NULL, NULL),
(440394, 130202, 'بلوز شلوار گرم', 5, NULL, NULL),
(440400, 130202, 'قاشق يکبار مصرف', 5, NULL, NULL),
(440416, 130202, 'رنگ سفيد', 6, NULL, NULL),
(440437, 130202, 'استامبلي بنايي', 5, NULL, NULL),
(440448, 130202, 'باطري کتابي', 5, NULL, NULL),
(440453, 130202, 'زانو 1و1/2', 5, NULL, NULL),
(13010201, 130102, 'صصثثضصث', 4, NULL, NULL),
(13020501, 130205, 'کالای جدید', 1, NULL, NULL),
(13020502, 130205, 'ییییی', 1, NULL, NULL),
(13020601, 130206, 'ثثثثضصثضصث', 6, NULL, NULL),
(13020701, 130207, 'دستگاه شاخه', 3, '20.000000000', 4);

-- --------------------------------------------------------

--
-- Table structure for table `itemcategory`
--

CREATE TABLE `itemcategory` (
  `categoryId` bigint(20) NOT NULL,
  `categoryParentId` bigint(20) DEFAULT NULL,
  `description` varchar(40) COLLATE utf8_persian_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_persian_ci;

--
-- Dumping data for table `itemcategory`
--

INSERT INTO `itemcategory` (`categoryId`, `categoryParentId`, `description`) VALUES
(1301, NULL, 'ابزار آلات'),
(1302, NULL, 'لوله و اتصالات'),
(130101, NULL, 'عنوان گروه ابزار آلات'),
(130102, NULL, 'ساختمانی'),
(130103, NULL, 'بهداشتی و نظافتی'),
(130201, 1302, 'اتصالات گالوانیزه'),
(130202, NULL, 'اتصالات لوله سبز'),
(130203, 1302, 'لوله و اتصالات گازی'),
(130205, NULL, 'متن گروه ویرایش شده جدید'),
(130206, NULL, 'گروه جدید'),
(130207, NULL, 'گروه کالا');

-- --------------------------------------------------------

--
-- Table structure for table `item_price`
--

CREATE TABLE `item_price` (
  `id` bigint(20) NOT NULL,
  `price_type` int(11) DEFAULT NULL,
  `date` bigint(20) DEFAULT NULL,
  `item_id` bigint(20) DEFAULT NULL,
  `price` decimal(10,0) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_persian_ci;

--
-- Dumping data for table `item_price`
--

INSERT INTO `item_price` (`id`, `price_type`, `date`, `item_id`, `price`) VALUES
(6, 1, 13990101, 44052, '2000'),
(7, 2, 13990101, 44052, '2000'),
(8, 3, 13990101, 44052, '2000'),
(10, 4, 13990101, 44052, '2000'),
(11, 4, 13990301, 44052, '434');

-- --------------------------------------------------------

--
-- Table structure for table `payments`
--

CREATE TABLE `payments` (
  `refType` int(11) DEFAULT NULL,
  `refId` bigint(20) UNSIGNED DEFAULT NULL,
  `contactId` bigint(20) UNSIGNED NOT NULL,
  `creditor` decimal(16,4) DEFAULT NULL,
  `debtor` decimal(16,4) DEFAULT NULL,
  `objectId` bigint(20) NOT NULL,
  `objectType` int(11) NOT NULL COMMENT 'انبار، صندوق، بانک و...',
  `date` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_persian_ci;

--
-- Dumping data for table `payments`
--

INSERT INTO `payments` (`refType`, `refId`, `contactId`, `creditor`, `debtor`, `objectId`, `objectType`, `date`) VALUES
(1, 21, 1106, '0.0000', '300.0000', 1, 10, 13990829),
(1, 21, 1106, '300.0000', '0.0000', 1, 20, 13990829),
(1, 20, 1105, '100.0000', '0.0000', 1, 10, 13990829),
(1, 20, 1105, '0.0000', '100.0000', 1, 20, 13990829),
(1, 22, 1105, '3600.0000', '0.0000', 1, 10, 13990829),
(1, 22, 1105, '0.0000', '3600.0000', 1, 20, 13990829),
(1, 23, 1105, '1500.0000', '0.0000', 1, 10, 13990829),
(1, 23, 1105, '0.0000', '1500.0000', 1, 20, 13990829),
(1, 24, 1105, '20000.0000', '0.0000', 1, 10, 13990830),
(1, 24, 1105, '0.0000', '20000.0000', 1, 20, 13990830),
(1, 25, 1106, '0.0000', '20000.0000', 1, 10, 13990830),
(1, 25, 1106, '20000.0000', '0.0000', 1, 20, 13990830);

-- --------------------------------------------------------

--
-- Table structure for table `unit`
--

CREATE TABLE `unit` (
  `unitId` int(11) NOT NULL,
  `unitName` varchar(30) COLLATE utf8_persian_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_persian_ci;

--
-- Dumping data for table `unit`
--

INSERT INTO `unit` (`unitId`, `unitName`) VALUES
(1, 'بسته'),
(2, 'دست'),
(3, 'دستگاه'),
(4, 'شاخه'),
(5, 'عدد'),
(6, 'کیلوگرم'),
(7, 'لیتر'),
(8, 'متر');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `bank`
--
ALTER TABLE `bank`
  ADD PRIMARY KEY (`bankId`),
  ADD UNIQUE KEY `bankInfo` (`bankName`);

--
-- Indexes for table `bankaccount`
--
ALTER TABLE `bankaccount`
  ADD PRIMARY KEY (`bankAccountId`),
  ADD KEY `bankId` (`bankId`),
  ADD KEY `branchId` (`branchId`);

--
-- Indexes for table `bankbranch`
--
ALTER TABLE `bankbranch`
  ADD PRIMARY KEY (`branchId`),
  ADD KEY `idx_branch_bank` (`bankId`);

--
-- Indexes for table `chequebook`
--
ALTER TABLE `chequebook`
  ADD PRIMARY KEY (`chequeBookId`);

--
-- Indexes for table `config`
--
ALTER TABLE `config`
  ADD KEY `config_configid_IDX` (`configid`) USING BTREE;

--
-- Indexes for table `contact`
--
ALTER TABLE `contact`
  ADD PRIMARY KEY (`contactId`),
  ADD UNIQUE KEY `indx_contact_national` (`nationalId`);

--
-- Indexes for table `invoice`
--
ALTER TABLE `invoice`
  ADD PRIMARY KEY (`invoiceid`);

--
-- Indexes for table `invoicedetail`
--
ALTER TABLE `invoicedetail`
  ADD PRIMARY KEY (`detailId`),
  ADD KEY `idx_invoice_detail_contact` (`contactId`),
  ADD KEY `itemId` (`itemId`),
  ADD KEY `idx_invoicedetail_invoiceid` (`invoiceid`) USING BTREE;

--
-- Indexes for table `item`
--
ALTER TABLE `item`
  ADD PRIMARY KEY (`itemId`),
  ADD KEY `idx_item_category` (`categoryId`),
  ADD KEY `unit1` (`unit1`),
  ADD KEY `unit2` (`unit2`);

--
-- Indexes for table `itemcategory`
--
ALTER TABLE `itemcategory`
  ADD PRIMARY KEY (`categoryId`),
  ADD KEY `categoryParentId` (`categoryParentId`);

--
-- Indexes for table `item_price`
--
ALTER TABLE `item_price`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `item_price_un` (`price_type`,`date`,`item_id`);

--
-- Indexes for table `payments`
--
ALTER TABLE `payments`
  ADD KEY `payments_invoiceId_IDX` (`refId`) USING BTREE,
  ADD KEY `payments_contactId_IDX` (`contactId`) USING BTREE,
  ADD KEY `payments_objectType_IDX` (`objectType`) USING BTREE,
  ADD KEY `payments_documentId_IDX` (`refType`) USING BTREE;

--
-- Indexes for table `unit`
--
ALTER TABLE `unit`
  ADD PRIMARY KEY (`unitId`),
  ADD UNIQUE KEY `idx_unitname` (`unitName`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `contact`
--
ALTER TABLE `contact`
  MODIFY `contactId` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1111;
--
-- AUTO_INCREMENT for table `invoice`
--
ALTER TABLE `invoice`
  MODIFY `invoiceid` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=26;
--
-- AUTO_INCREMENT for table `invoicedetail`
--
ALTER TABLE `invoicedetail`
  MODIFY `detailId` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4532;
--
-- AUTO_INCREMENT for table `itemcategory`
--
ALTER TABLE `itemcategory`
  MODIFY `categoryId` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=130208;
--
-- AUTO_INCREMENT for table `item_price`
--
ALTER TABLE `item_price`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;
--
-- AUTO_INCREMENT for table `unit`
--
ALTER TABLE `unit`
  MODIFY `unitId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `bankaccount`
--
ALTER TABLE `bankaccount`
  ADD CONSTRAINT `cst_account_bank` FOREIGN KEY (`branchId`) REFERENCES `bankbranch` (`branchId`) ON UPDATE CASCADE,
  ADD CONSTRAINT `cst_account_branch` FOREIGN KEY (`bankId`) REFERENCES `bank` (`bankId`) ON UPDATE CASCADE;

--
-- Constraints for table `bankbranch`
--
ALTER TABLE `bankbranch`
  ADD CONSTRAINT `cst_branch_bank` FOREIGN KEY (`bankId`) REFERENCES `bank` (`bankId`) ON UPDATE CASCADE;

--
-- Constraints for table `invoicedetail`
--
ALTER TABLE `invoicedetail`
  ADD CONSTRAINT `cst_invoice_detail_contact_id` FOREIGN KEY (`contactId`) REFERENCES `contact` (`contactId`) ON UPDATE CASCADE,
  ADD CONSTRAINT `cst_invoice_detail_item_id` FOREIGN KEY (`itemId`) REFERENCES `item` (`itemId`) ON UPDATE CASCADE;

--
-- Constraints for table `item`
--
ALTER TABLE `item`
  ADD CONSTRAINT `cst_item_category` FOREIGN KEY (`categoryId`) REFERENCES `itemcategory` (`categoryId`) ON UPDATE CASCADE,
  ADD CONSTRAINT `cst_unit_1` FOREIGN KEY (`unit1`) REFERENCES `unit` (`unitId`) ON UPDATE CASCADE,
  ADD CONSTRAINT `cst_unit_2` FOREIGN KEY (`unit2`) REFERENCES `unit` (`unitId`) ON UPDATE CASCADE;

--
-- Constraints for table `itemcategory`
--
ALTER TABLE `itemcategory`
  ADD CONSTRAINT `itemcategory_fk` FOREIGN KEY (`categoryParentId`) REFERENCES `itemcategory` (`categoryId`) ON DELETE NO ACTION ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
