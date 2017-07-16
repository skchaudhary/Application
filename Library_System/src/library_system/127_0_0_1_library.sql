--
-- Database: `library`
--
CREATE DATABASE IF NOT EXISTS `library` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `library`;

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

CREATE TABLE `admin` (
  `username` varchar(25) NOT NULL,
  `password` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` (`username`, `password`) VALUES
('admin', 'admin123');

-- --------------------------------------------------------

--
-- Table structure for table `book`
--

CREATE TABLE `book` (
  `book_callno` int(8) NOT NULL,
  `book_name` varchar(50) NOT NULL,
  `author_name` varchar(50) NOT NULL,
  `publisher` varchar(50) NOT NULL,
  `quantity` int(8) NOT NULL,
  `issued_book` int(8) NOT NULL,
  `added_date` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `book`
--

INSERT INTO `book` (`book_callno`, `book_name`, `author_name`, `publisher`, `quantity`, `issued_book`, `added_date`) VALUES
(100, 'operating system', 'kk', 'mcg', 48, 2, '2016-10-23'),
(101, 'graph theory', 'jj', 'lkv', 34, 1, '2016-10-23');

-- --------------------------------------------------------

--
-- Table structure for table `librarian`
--

CREATE TABLE `librarian` (
  `username` varchar(20) NOT NULL,
  `password` varchar(20) NOT NULL,
  `name` varchar(50) NOT NULL,
  `email` varchar(50) NOT NULL,
  `address` varchar(100) NOT NULL,
  `city` varchar(50) NOT NULL,
  `contact` bigint(12) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `librarian`
--

INSERT INTO `librarian` (`username`, `password`, `name`, `email`, `address`, `city`, `contact`) VALUES
('sanjay', 'sanjay', 'sanjay chaudhary', 'sanjay@gmail.com', 'basti', 'basti', 8131037012);

-- --------------------------------------------------------

--
-- Table structure for table `student`
--

CREATE TABLE `student` (
  `book_callno` int(8) NOT NULL,
  `student_id` varchar(15) NOT NULL,
  `student_name` varchar(50) NOT NULL,
  `student_contact` bigint(12) NOT NULL,
  `issued_date` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `student`
--

INSERT INTO `student` (`book_callno`, `student_id`, `student_name`, `student_contact`, `issued_date`) VALUES
(100, '1000', 'sanjay', 8131037012, '2016-10-23'),
(101, '1000', 'sanjay', 849845985, '2016-10-23'),
(100, 'csd48', 'sanjau ds', 8484981490, '2016-10-23');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`username`);

--
-- Indexes for table `book`
--
ALTER TABLE `book`
  ADD PRIMARY KEY (`book_callno`);

--
-- Indexes for table `librarian`
--
ALTER TABLE `librarian`
  ADD PRIMARY KEY (`username`);
