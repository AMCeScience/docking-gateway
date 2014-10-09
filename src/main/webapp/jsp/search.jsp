<div class="search">
    <form class="search_form">
        <div class="fourth_col">
            <label for="search_terms">Search</label>
            <input name="search_terms" type="text" value=""/>
        </div>

        <div class="fourth_col">
            <label for="date_started">Date Started</label>
            <select name="date_started">
                <option value="descending">Descending</option>
                <option value="ascending">Ascending</option>
            </select>
        </div>

        <div class="fourth_col">
            <label for="project_name">Project Name</label>
            <select name="project_name">
                <option value="descending">Descending</option>
                <option value="ascending">Ascending</option>
            </select>
        </div>

		<div class="fourth_col">
		    <label for="status">Status</label>
		    <select name="status">
		        <option value="all">All</option>
		        <option value="in preparation">In Preparation</option>
		        <option value="in progress">In Progress</option>
		        <option value="on hold">On Hold</option>
		        <option value="done">Done</option>
		        <option value="failed">Failed</option>
		        <option value="aborted">Aborted</option>
		        <option class="select-dash" disabled="disabled">----</option>
		        <option value="running">Running</option>
		        <option value="stopped">Stopped</option>
		    </select>
		</div>

        <div class="clear"></div>
    </form>

    <hr/>
</div>