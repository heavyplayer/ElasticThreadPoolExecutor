# ElasticThreadPoolExecutor

A more dynamic ThreadPoolExecutor implementation.

## Behavior

Exactly like the regular ThreadPoolExecutor, with one small but very important difference: instead of only spawning more threads when the working queue is full, this implementation spawns more threads when all core threads are occupied (up to the maximum threads you specify).

Example: You specify that the core pool size is 2, and that the maximum size is 4.

<table>
    <tr>
        <th>Action</td>
        <th>Core threads</td>
        <th>Working threads</td>
        <th>Queue size</td>
    </tr>
    <tr>
    	<td>Submit long task</td>
    	<td>2</td>
    	<td>1</td>
    	<td>0</td>
    </tr>
    <tr>
    	<td>Submit long task</td>
    	<td>2</td>
    	<td>2</td>
    	<td>0</td>
    </tr>
    <tr>
    	<td>Submit long task</td>
    	<td>3</td>
    	<td>3</td>
    	<td>0</td>
    </tr>
	<tr>
    	<td>Submit long task</td>
    	<td>4</td>
    	<td>4</td>
    	<td>0</td>
    </tr>
	<tr>
    	<td>Submit long task</td>
    	<td>4</td>
    	<td>4</td>
    	<td>1</td>
    </tr>
	<tr>
    	<td>Submit long task</td>
    	<td>4</td>
    	<td>4</td>
    	<td>2</td>
    </tr>
    <tr>
	    <td colspan="4"><i>Wait for tasks to complete and for the specified thread timeout</i></td>
    </tr>
	<tr>
    	<td>Nothing</td>
    	<td>2</td>
    	<td>0</td>
    	<td>0</td>
    </tr>
</table>

That's it. If you still have doubts about how it works, don't forget to check the tests.

<hr/>

Copyright © 2010 Heavy Player, released under the MIT license