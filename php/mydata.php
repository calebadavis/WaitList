
<?php
//  $cust_data = file_get_contents('./cust_list.txt', true);
//  echo $cust_data;
$filename = 'cust_list.csv';

// The nested array to hold all the arrays
$the_big_array = []; 

// Open the file for reading
if (($h = fopen("{$filename}", "r")) !== FALSE) 
{
  // Each line in the file is converted into an individual array that we call $data
  // The items of the array are comma separated
  while (($data = fgetcsv($h, 1000, ",")) !== FALSE) 
  {
    // Each individual array is being pushed into the nested array
    $the_big_array[] = $data;		
  }

  // Close the file
  fclose($h);
}

// Display the code in a readable format

echo "<table>
<tr>
<th>Last</th>
<th>First</th>
<th>Phone</th>
</tr>";
for ($i=0; $i<count($the_big_array, 0); $i++)
{
    echo '<tr>';
    for ($j=0; $j<3; $j++)
    {
        echo '<td>'.$the_big_array[$i][$j].'</td>';
    }
    echo '</tr>';
}
echo '</table>';
?>