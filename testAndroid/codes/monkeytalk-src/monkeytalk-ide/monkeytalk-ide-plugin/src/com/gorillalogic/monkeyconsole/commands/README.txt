For each Handler class, you should make a Command extension in plugin.xml 
Your handler class's name is the "defaultHandler" for the Command.

You use that command's "id" to bind to toolbars, menus, buttons, etc.

You should use Handlers instead of Actions whenever possible. 