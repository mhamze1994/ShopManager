-- phpMyAdmin SQL Dump
-- version 4.5.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Dec 10, 2020 at 03:12 PM
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
CREATE DEFINER=`root`@`localhost` PROCEDURE `anbar_gardani_update` (IN `in_item_id` BIGINT, IN `in_count_1` DECIMAL, IN `in_count_2` DECIMAL, IN `in_count_3` DECIMAL)  begin
	replace into anbar_gardani (item_id , count_1 , count_2 , count_3) values (in_item_id , in_count_1,in_count_2,in_count_3);
END$$

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

CREATE DEFINER=`root`@`localhost` PROCEDURE `check_year` (IN `in_date_start` BIGINT, IN `in_date_end` BIGINT)  begin
	declare msg varchar(128) default "date_out_of_range";
	
	if in_date_start < PERIOD_START() then
		signal SQLSTATE '45000' SET MESSAGE_TEXT = msg;
	end if;

	if in_date_end > PERIOD_END() then
		signal SQLSTATE '45000' SET MESSAGE_TEXT = msg;
	end if;

END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `cheque_insert` (IN `in_bank_id` BIGINT, IN `in_value` DECIMAL, IN `in_type` INT, IN `in_serial` BIGINT, IN `in_invoice_id` BIGINT, IN `in_date` BIGINT)  begin

	insert into cheque (cheque.bankid , value , `type` , serial , invoiceid ,`date`) 
		values (in_bank_id , in_value , in_type , in_serial , in_invoice_id , in_date );
end$$

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

CREATE DEFINER=`root`@`localhost` PROCEDURE `get_balance` (IN `in_object_type` INT, IN `in_object_id` BIGINT, OUT `balance` DECIMAL)  begin

	
	select (sum(p.creditor) - sum(p.debtor)) into balance from payments p 
		where p.`objectType` = in_object_type and p.`objectId` = in_object_id

		group by `objectType`;
	
	if balance is null then
	 	set balance = 0;
	end if;
	
	
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `get_item_stock` (IN `in_item_id` BIGINT, OUT `out_stock` DECIMAL)  begin
	
	if in_item_id is null then
		select in_item_id, sum(`suAmount`)  from invoicedetail where `itemId` = (case when in_item_id is null then `itemId` else in_item_id end );
	else	
		select sum(`suAmount`) into out_stock from invoicedetail where `itemId` = in_item_id;
		if out_stock is null then
			set out_stock = 0;
		end if;
	end if;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `invoicedetail_delete` (IN `in_invoice` BIGINT)  begin
	delete from invoice where invoiceid = in_invoice;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `invoicedetail_insert` (IN `in_date` BIGINT, IN `in_contact` BIGINT, IN `in_operation` INT, IN `in_item` BIGINT, IN `in_amount` DECIMAL, IN `in_unit_id` INT, IN `in_ratio` DECIMAL, IN `in_unit_price` DECIMAL, IN `in_su_price` DECIMAL, IN `in_ref_id` BIGINT, IN `in_invoice` BIGINT)  begin
	
	call check_year(in_date , in_date);
	
	INSERT INTO shopmanager.invoicedetail
		(`date`, `contactId`, `operationType`, `itemId`,`amount`,`unitid`,`ratio`,`unitprice`, `suAmount`, `suPrice`, `refDetailId`, invoiceid)
	VALUES(in_date, in_contact, in_operation, in_item, in_amount , in_unit_id, in_ratio,in_unit_price, in_amount * in_ratio , in_su_price, in_ref_id, in_invoice);

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
	
	
	call check_year(in_date , in_date);
	
	insert into invoice 
		(invoice.invoicedate , invoice.contact , invoice.operationtype , invoice.totalCost ) 
		values 
		(in_date , in_contact , in_type , in_total_cost);
	
	set out_invoice_id = last_insert_id();
	
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `invoice_update` (IN `in_invoice_id` BIGINT, IN `in_date` BIGINT, IN `in_contact` BIGINT, IN `in_type` INT, IN `in_total_cost` DECIMAL)  begin
	
	call check_year(in_date , in_date);

	update invoice set 
		invoice.invoicedate = in_date, invoice.contact = in_contact, invoice.operationtype = in_type, invoice.totalCost = in_total_cost 
		where invoice.invoiceid = in_invoice_id;
	
	delete from invoicedetail where invoiceid = in_invoice_id;
	delete from payments where `refId` = in_invoice_id and `refType` = 1;
	delete from cheque where invoiceid = in_invoice_id;


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

CREATE DEFINER=`root`@`localhost` PROCEDURE `load_anbar_gardani` ()  begin
	select i.`itemId`,i.description,ag.count_1,ag.count_2,ag.count_3 from anbar_gardani ag right join item i on ag.item_id = i.`itemId`;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `ops_cashbox` (INOUT `in_cashbox_id` BIGINT, IN `in_cashbox_title` VARCHAR(128), IN `in_op` INT)  begin
	
	if in_op = 1 then
		insert into cashbox (cashbox.title) values (in_cashbox_title);
		set in_cashbox_id = last_insert_id();
	elseif in_op = 2 then
		update cashbox set cashbox.title = in_cashbox_title where cashboxid = in_cashbox_id;
	elseif in_op = 3 then
		delete from cashbox where cashboxid = in_cashbox_id;
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
	
	call check_year(in_date , in_date);
	
	insert into payments 
	( `reftype` , `refId` , `contactId`,`creditor` , `debtor`,`objectId`,`objectType` ,`date`) 
	values 
	(reftype , refid , contact , creditor , debtor , object_id , object_type , in_date);
	
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `report_customer_item_bill` (IN `in_date_start` BIGINT, IN `in_date_end` BIGINT, IN `in_contact_id` BIGINT, IN `in_item_id` BIGINT, IN `in_operation` INT)  begin
	
	
	set in_date_start = GREATEST (in_date_start , PERIOD_START());
	set in_date_end   = LEAST    (in_date_end   , PERIOD_END());
	
	
	select `invoiceid`, date , invoicedetail.`contactId` , trim(concat(contact.name ," ", contact.lastname)), `operationType` , item.`itemId` , item.description, amount , unit.`unitName` , unitprice , suAmount ,`suAmount` * `suPrice` as totalPrice from invoicedetail 
	join item on invoicedetail.`itemId` = item.`itemId`
	join unit on invoicedetail.unitid = unit.`unitId`
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
		`date` <= (case when in_date_end is null then ~0 else in_date_end end)
		
	order by date asc , invoiceid asc;
	
	
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `report_item_price` (IN `in_item_id` BIGINT)  BEGIN
	select * from item_price ip where ip.item_id = in_item_id order by `date` desc; 
	END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `report_payment_bill` (IN `in_contact_id` BIGINT, IN `in_date_start` BIGINT, IN `in_date_end` BIGINT)  begin
	
	set in_date_start = GREATEST (in_date_start , PERIOD_START());
	set in_date_end   = LEAST    (in_date_end   , PERIOD_END());

	
	select `refType`, idet.`operationType`, `refId`,max(p.`date`) as d , concat(c.`contactId`," - ", trim(concat(c.name," ",c.lastname))) as cName, 
		sum(p.creditor),sum(p.debtor), sum(creditor)-sum(debtor) 
		from payments p 
		join contact c on p.`contactId` = c.`contactId`
		join invoicedetail idet on idet.invoiceid = p.`refId`
		where
		c.`contactId` = (case when in_contact_id is null then c.`contactId` else in_contact_id end)
		and
		p.`date` >= (case when in_date_start is null then 0 else in_date_start end)
		and
		p.`date` <= (case when in_date_end is null then ~0 else in_date_end end)
	group by `refId` order by d desc , idet.invoiceid desc;

END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `report_payment_sum` (IN `in_contact_id` BIGINT)  begin
	select concat(c.`contactId`," - ", trim(concat(c.name," ",c.lastname))) as cName, 
		sum(p.creditor),sum(p.debtor), sum(creditor)-sum(debtor) 
		from payments p 
		join contact c on p.`contactId` = c.`contactId` 
		where 
		c.`contactId` = (case when in_contact_id is null then c.`contactId` else in_contact_id end)
		and
		p.`date` >= PERIOD_START() 
		and 
		p.`date` <= PERIOD_END()
	group by c.`contactId` ;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `report_per_item_profit` (IN `in_item_id` BIGINT, IN `in_date_start` BIGINT, IN `in_date_end` BIGINT)  begin
	
	set in_date_start = GREATEST (in_date_start , PERIOD_START());
	set in_date_end   = LEAST    (in_date_end   , PERIOD_END());

select ts.`itemId`, i.description , sum(`suAmount_sell`) , sum(total_price_sell) / sum(`suAmount_sell`) as avgSell , tb.total_price_buy / tb.total_su_amount_buy as avgBuy
	from view_item_avg_sell ts 
	join view_item_avg_buy tb on ts.`itemId` = tb.`itemId` 
	join item i on ts.`itemId` = i.`itemId`
	where 
	date_sell >= (case when in_date_start is null then 0 else in_date_start end) and 
	date_sell <= (case when in_date_end is null then ~0 else in_date_end end) and 
	ts.`itemId` = (case when in_item_id is null then ts.`itemId` else in_item_id end)
	group by ts.`itemId`;
	
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `search_contact` (IN `in_pattern` VARCHAR(256))  begin
	
	select * from contact c where c.full_info like concat('%',in_pattern,'%') or c.full_info like concat('%',replace(in_pattern , " ",""),'%') limit 10;
	
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `search_contact_id` (IN `in_contact_id` BIGINT)  begin
	
	select * from contact c where `contactId` = in_contact_id;
	
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `search_invoice` (IN `in_invoice_id` BIGINT)  begin	
	
	select * from invoice where invoiceid = in_invoice_id and invoicedate >= PERIOD_START() and invoicedate <= PERIOD_END();

	select 
		`detailId`,`date`,`contactId`,`operationType`,`itemId`,`amount`,`unitid`,`ratio`,`unitprice`,`suAmount`,`suPrice`,`refDetailId`,`invoiceid`
	from invoicedetail where invoiceid = in_invoice_id;

	select  `reftype` , `refId` , `contactId`,`creditor` , `debtor`,`objectId`,`objectType` ,`date` from payments 
		where `refId` = in_invoice_id and `refType` = 1;
	
	select * from cheque where invoiceid = in_invoice_id;

	
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

CREATE DEFINER=`root`@`localhost` PROCEDURE `search_latest_contact_item_details` (IN `in_contactId` BIGINT, IN `in_itemId` BIGINT, IN `in_operation` INT)  begin
	select 
	`detailId`,`date`,`contactId`,`operationType`,`itemId`,`amount`,`unitid`,`ratio`,`unitprice`,`suAmount`,`suPrice`,`refDetailId`,`invoiceid`
	from invoicedetail
		where 
		`contactId` = in_contactId 
		and 
		`itemId` = in_itemId 
		and 
		`operationType` = in_operation 
		and
		`date` >= PERIOD_START()
		and
		`date` <= PERIOD_END()

order by `date` desc limit 5 ;
	
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `select_cashbox` (IN `in_cashboxid` BIGINT)  begin
	
	select cashboxid, title from cashbox 
		where cashboxid = (case when in_cashboxid is null then cashbox.cashboxid else in_cashboxid end);
	
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `select_catalog` (IN `in_pattern` VARCHAR(256))  begin
	select itemid , item.`categoryId` , item.description as itemDesc , unit1 , ratio1 , unit2 , itemcategory.description as catDesc  
			from item left join itemcategory 
				on itemcategory.`categoryId` = item.`categoryId` where item.description like concat('%',in_pattern,'%');

END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `select_category_all_leaf` ()  begin
	SELECT * from itemcategory where categoryId not in (SELECT categoryParentId FROM `itemcategory` WHERE categoryParentId is not null) order by itemcategory.description;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `select_unit_all` ()  begin
	select * from unit u;
END$$

--
-- Functions
--
CREATE DEFINER=`root`@`localhost` FUNCTION `PERIOD_END` () RETURNS BIGINT(20) begin
	declare e bigint;
	select date_end into e from fiscal_period where status = 1;
	return e;
END$$

CREATE DEFINER=`root`@`localhost` FUNCTION `PERIOD_START` () RETURNS BIGINT(20) begin
	declare s bigint;
	select date_start into s from fiscal_period where status = 1;
	return s;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `anbar_gardani`
--

CREATE TABLE `anbar_gardani` (
  `item_id` bigint(20) NOT NULL,
  `count_1` decimal(18,9) DEFAULT NULL,
  `count_2` decimal(18,9) DEFAULT NULL,
  `count_3` decimal(18,9) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_persian_ci;

--
-- Dumping data for table `anbar_gardani`
--

INSERT INTO `anbar_gardani` (`item_id`, `count_1`, `count_2`, `count_3`) VALUES
(13020801, '5.000000000', '0.000000000', '0.000000000'),
(13020802, '3.000000000', '0.000000000', '0.000000000'),
(13020803, '2.000000000', '0.000000000', '0.000000000'),
(13020804, '1.000000000', '0.000000000', '0.000000000'),
(13020805, '5.000000000', '0.000000000', '0.000000000'),
(13020806, '23.000000000', '0.000000000', '0.000000000'),
(13020901, '3.000000000', '0.000000000', '0.000000000'),
(13020902, '2.000000000', '0.000000000', '0.000000000'),
(13020903, '0.000000000', '0.000000000', '0.000000000'),
(13020904, '0.000000000', '0.000000000', '0.000000000'),
(13020905, '0.000000000', '0.000000000', '0.000000000'),
(13020906, '0.000000000', '0.000000000', '0.000000000'),
(13021301, '0.000000000', '0.000000000', '0.000000000'),
(13021302, '0.000000000', '0.000000000', '0.000000000'),
(13021303, '0.000000000', '0.000000000', '0.000000000'),
(13021401, '0.000000000', '0.000000000', '0.000000000'),
(13021402, '0.000000000', '0.000000000', '0.000000000'),
(13021403, '0.000000000', '0.000000000', '0.000000000'),
(13021404, '0.000000000', '0.000000000', '0.000000000'),
(13021405, '0.000000000', '0.000000000', '0.000000000'),
(13021501, '0.000000000', '0.000000000', '0.000000000'),
(13021502, '0.000000000', '0.000000000', '0.000000000'),
(13021701, '0.000000000', '0.000000000', '0.000000000'),
(13021702, '0.000000000', '0.000000000', '0.000000000'),
(13021703, '0.000000000', '0.000000000', '0.000000000'),
(13021704, '0.000000000', '0.000000000', '0.000000000'),
(13021705, '0.000000000', '0.000000000', '0.000000000'),
(13021801, '0.000000000', '0.000000000', '0.000000000'),
(13021802, '0.000000000', '0.000000000', '0.000000000'),
(13021901, '0.000000000', '0.000000000', '0.000000000'),
(13021902, '0.000000000', '0.000000000', '0.000000000'),
(13021903, '0.000000000', '0.000000000', '0.000000000'),
(13021904, '0.000000000', '0.000000000', '0.000000000'),
(13022001, '0.000000000', '0.000000000', '0.000000000'),
(13022002, '0.000000000', '0.000000000', '0.000000000'),
(13022101, '0.000000000', '0.000000000', '0.000000000'),
(13022102, '0.000000000', '0.000000000', '0.000000000');

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
-- Table structure for table `cashbox`
--

CREATE TABLE `cashbox` (
  `cashboxid` bigint(20) NOT NULL,
  `title` varchar(100) COLLATE utf8_persian_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_persian_ci;

--
-- Dumping data for table `cashbox`
--

INSERT INTO `cashbox` (`cashboxid`, `title`) VALUES
(5, 'صندوق 1'),
(6, 'صندوق 2');

-- --------------------------------------------------------

--
-- Table structure for table `cheque`
--

CREATE TABLE `cheque` (
  `chequeid` bigint(20) NOT NULL,
  `bankid` bigint(20) DEFAULT NULL,
  `value` decimal(10,0) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `serial` bigint(20) DEFAULT NULL,
  `invoiceid` bigint(20) DEFAULT NULL,
  `date` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_persian_ci;

--
-- Dumping data for table `cheque`
--

INSERT INTO `cheque` (`chequeid`, `bankid`, `value`, `type`, `serial`, `invoiceid`, `date`) VALUES
(10, 1010, '370000', 2, 123, 178, 13990503),
(11, 1032, '50000', 2, 123, 178, 13990506),
(12, 1003, '950000', 2, 123123, 178, 13990512);

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
('print_vp', '5'),
('print_hp', '5'),
('print_paper_w', '450'),
('shop_title', 'خدمات کامپیوتری'),
('print_paper_h', '900');

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
(1112, NULL, '', 139901010000, 'پی سی سنتر', 'pc center', '', 'پیسیسنترpccenter', ''),
(1113, NULL, '', 139901010000, 'مجتبی', 'غلامی حقیقی (آپادانا)', '', 'مجتبیغلامیحقیقی(آپادانا)', ''),
(1114, NULL, '', 139901010000, 'متفرقه', '', '', 'متفرقه', ''),
(1115, NULL, '', 139901010000, 'سیستم', '', '', 'سیستم', ''),
(1116, 2315212552, 'مرودشت-سایپا', 139901010000, 'رضا ', 'فرهادپور', 'علی', 'رضافرهادپور', '09179276503');

-- --------------------------------------------------------

--
-- Table structure for table `fiscal_period`
--

CREATE TABLE `fiscal_period` (
  `period` int(11) DEFAULT NULL,
  `date_start` bigint(20) DEFAULT NULL,
  `date_end` bigint(20) DEFAULT NULL,
  `status` int(11) DEFAULT NULL COMMENT '1:active , 2:inactive, 3:close',
  `title` varchar(100) COLLATE utf8_persian_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_persian_ci;

--
-- Dumping data for table `fiscal_period`
--

INSERT INTO `fiscal_period` (`period`, `date_start`, `date_end`, `status`, `title`) VALUES
(1399, 13990101, 13991230, 1, NULL);

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
(221, 13990203, 1115, 70, '0.0000'),
(222, 13990915, 1112, 10, '20000.0000'),
(223, 13990915, 1113, 20, '-25000.0000');

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
  `amount` decimal(18,9) DEFAULT NULL,
  `unitid` int(11) DEFAULT NULL,
  `ratio` decimal(18,9) DEFAULT NULL,
  `unitprice` decimal(18,9) NOT NULL,
  `suAmount` decimal(18,9) DEFAULT NULL COMMENT 'smallest unit amount',
  `suPrice` decimal(16,4) DEFAULT NULL,
  `refDetailId` bigint(20) DEFAULT NULL,
  `invoiceid` bigint(20) UNSIGNED DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_persian_ci;

--
-- Dumping data for table `invoicedetail`
--

INSERT INTO `invoicedetail` (`detailId`, `date`, `contactId`, `operationType`, `itemId`, `amount`, `unitid`, `ratio`, `unitprice`, `suAmount`, `suPrice`, `refDetailId`, `invoiceid`) VALUES
(4847, 13990915, 1112, 10, 13020803, '1.000000000', 5, '1.000000000', '20000.000000000', '1.000000000', '20000.0000', NULL, 222),
(4848, 13990915, 1113, 20, 13020803, '-1.000000000', 5, '1.000000000', '25000.000000000', '-1.000000000', '25000.0000', NULL, 223);

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
(13020801, 130208, 'کیبورد A4 Tech KR-85', 5, NULL, NULL),
(13020802, 130208, 'کیبورد Beyond Combo BMK-4420', 5, NULL, NULL),
(13020803, 130208, 'کیبورد SADATA Combo SKM-1554', 5, NULL, NULL),
(13020804, 130208, 'کیبورد SADATA SK-1600', 5, NULL, NULL),
(13020805, 130208, 'کیبورد sadata 1500S', 5, NULL, NULL),
(13020806, 130208, 'کیبورد Sadata SK1500', 5, NULL, NULL),
(13020901, 130209, 'ماوس DATIS E-300', 5, NULL, NULL),
(13020902, 130209, 'ماوس Genius G-4111', 5, NULL, NULL),
(13020903, 130209, 'ماوس Dell Wired', 5, NULL, NULL),
(13020904, 130209, 'ماوس HP wireless', 5, NULL, NULL),
(13020905, 130209, 'ماوس Sadata SM-54', 5, NULL, NULL),
(13020906, 130209, 'ماوس Havit MS4252', 5, NULL, NULL),
(13021301, 130213, 'دسته بازی Macher Double MR-56', 5, NULL, NULL),
(13021302, 130213, 'دسته بازی Macher Shock Double MR-58', 5, NULL, NULL),
(13021303, 130213, 'دسته بازی Shock Single TSCO TG115', 5, NULL, NULL),
(13021401, 130214, 'پد ماوس طبی A4', 5, NULL, NULL),
(13021402, 130214, 'پد ماوس گیم - دور دوخت', 5, NULL, NULL),
(13021403, 130214, 'پد ماوس - طرح روزنامه', 5, NULL, NULL),
(13021404, 130214, 'پد ماوس کوچک - Logitech', 5, NULL, NULL),
(13021405, 130214, 'پد ماوس گیم - درجه 1', 5, NULL, NULL),
(13021501, 130215, 'هندزفری Duao X4S', 5, NULL, NULL),
(13021502, 130215, 'هندزفری E100', 5, NULL, NULL),
(13021701, 130217, 'فلش 16GB Apacer AH13C', 5, NULL, NULL),
(13021702, 130217, 'فلش 16GB Kingstar K210', 5, NULL, NULL),
(13021703, 130217, 'فلش 32GB apacer AH13C', 5, NULL, NULL),
(13021704, 130217, 'فلش 32GB Silicon Power T07', 5, NULL, NULL),
(13021705, 130217, 'فلش 64GB Kingstar K210', 5, NULL, NULL),
(13021801, 130218, 'شارژر سامسونگ', 5, NULL, NULL),
(13021802, 130218, 'شارژر هواوی', 5, NULL, NULL),
(13021901, 130219, 'کابل پرینتر 5 متر XP', 5, NULL, NULL),
(13021902, 130219, 'کابل پرینتر 1.5 متر XP', 5, NULL, NULL),
(13021903, 130219, 'کابل HDMI - یک و نیم متر Macher', 5, NULL, NULL),
(13021904, 130219, 'کابل HDMI - یک و نیم متر hp', 5, NULL, NULL),
(13022001, 130220, 'پاور بانک Energizer UE10046 10000mA', 5, NULL, NULL),
(13022002, 130220, 'نام پاور بانک جدید', 1, '5.000000000', 5),
(13022101, 130221, 'وب کم Cam ASDA', 5, NULL, NULL),
(13022102, 130216, 'مودم TP-link TD-8961N', 5, NULL, NULL),
(13022201, 130222, 'ماژیک فسفری', 1, '24.000000000', 5);

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
(130208, NULL, 'کیبورد'),
(130209, NULL, 'ماوس'),
(130210, NULL, 'نرم افزار کاربردی'),
(130211, NULL, 'نرم افزار بازی pc'),
(130213, NULL, 'گیم پد'),
(130214, NULL, 'ماوس پد'),
(130215, NULL, 'هندزفری'),
(130216, NULL, 'مودم'),
(130217, NULL, 'فلش مموری'),
(130218, NULL, 'شارژر موبایل'),
(130219, NULL, 'کابل'),
(130220, NULL, 'پاور بانک'),
(130221, NULL, 'وب کم'),
(130222, NULL, 'لوازم تحریری');

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
(1, 1, 13990914, 13022201, '3000'),
(2, 2, 13990914, 13022201, '60000');

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
(1, 221, 1115, '50000.0000', '0.0000', 5, 30, 13990203),
(1, 222, 1112, '20000.0000', '0.0000', 1, 10, 13990915),
(1, 222, 1112, '0.0000', '20000.0000', 5, 30, 13990915),
(1, 223, 1113, '0.0000', '25000.0000', 1, 10, 13990915),
(1, 223, 1113, '25000.0000', '0.0000', 5, 30, 13990915);

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
(9, 'پاکت'),
(2, 'دست'),
(3, 'دستگاه'),
(4, 'شاخه'),
(13, 'شل'),
(12, 'شیرینگ'),
(5, 'عدد'),
(10, 'کارتن'),
(11, 'کیسه'),
(6, 'کیلوگرم'),
(7, 'لیتر'),
(8, 'متر');

-- --------------------------------------------------------

--
-- Stand-in structure for view `view_item_avg_buy`
--
CREATE TABLE `view_item_avg_buy` (
`itemId` bigint(20) unsigned
,`total_su_amount_buy` decimal(40,9)
,`total_price_buy` decimal(56,13)
);

-- --------------------------------------------------------

--
-- Stand-in structure for view `view_item_avg_sell`
--
CREATE TABLE `view_item_avg_sell` (
`itemId` bigint(20) unsigned
,`date_sell` bigint(20) unsigned
,`suAmount_sell` decimal(18,9)
,`total_price_sell` decimal(34,13)
);

-- --------------------------------------------------------

--
-- Structure for view `view_item_avg_buy`
--
DROP TABLE IF EXISTS `view_item_avg_buy`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `view_item_avg_buy`  AS  select `i1`.`itemId` AS `itemId`,sum(`i1`.`suAmount`) AS `total_su_amount_buy`,sum((`i1`.`suAmount` * `i1`.`suPrice`)) AS `total_price_buy` from `invoicedetail` `i1` where (((`i1`.`operationType` = 10) or (`i1`.`operationType` = 11)) and (`i1`.`date` >= `PERIOD_START`()) and (`i1`.`date` <= `PERIOD_END`())) group by `i1`.`itemId` ;

-- --------------------------------------------------------

--
-- Structure for view `view_item_avg_sell`
--
DROP TABLE IF EXISTS `view_item_avg_sell`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `view_item_avg_sell`  AS  select `i1`.`itemId` AS `itemId`,`i1`.`date` AS `date_sell`,`i1`.`suAmount` AS `suAmount_sell`,(`i1`.`suAmount` * `i1`.`suPrice`) AS `total_price_sell` from `invoicedetail` `i1` where (((`i1`.`operationType` = 20) or (`i1`.`operationType` = 21)) and (`i1`.`date` >= `PERIOD_START`()) and (`i1`.`date` <= `PERIOD_END`())) ;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `anbar_gardani`
--
ALTER TABLE `anbar_gardani`
  ADD PRIMARY KEY (`item_id`);

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
-- Indexes for table `cashbox`
--
ALTER TABLE `cashbox`
  ADD PRIMARY KEY (`cashboxid`);

--
-- Indexes for table `cheque`
--
ALTER TABLE `cheque`
  ADD PRIMARY KEY (`chequeid`);

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
-- AUTO_INCREMENT for table `cashbox`
--
ALTER TABLE `cashbox`
  MODIFY `cashboxid` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;
--
-- AUTO_INCREMENT for table `cheque`
--
ALTER TABLE `cheque`
  MODIFY `chequeid` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;
--
-- AUTO_INCREMENT for table `contact`
--
ALTER TABLE `contact`
  MODIFY `contactId` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1117;
--
-- AUTO_INCREMENT for table `invoice`
--
ALTER TABLE `invoice`
  MODIFY `invoiceid` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=224;
--
-- AUTO_INCREMENT for table `invoicedetail`
--
ALTER TABLE `invoicedetail`
  MODIFY `detailId` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4849;
--
-- AUTO_INCREMENT for table `itemcategory`
--
ALTER TABLE `itemcategory`
  MODIFY `categoryId` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=130223;
--
-- AUTO_INCREMENT for table `item_price`
--
ALTER TABLE `item_price`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT for table `unit`
--
ALTER TABLE `unit`
  MODIFY `unitId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;
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
