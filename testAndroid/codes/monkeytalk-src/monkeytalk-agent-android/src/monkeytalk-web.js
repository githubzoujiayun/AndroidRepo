if (MonkeyTalk == undefined || adapters == undefined) {
    var MonkeyTalk = {};
    MonkeyTalk.didHandleTap = false;
    
    // ------------------------------------------------------
    // PLAYBACK
    // ------------------------------------------------------
    MonkeyTalk.finderKeys = ["monkeyId", "id", "name", "value", "title",
                             "class", "textContent", "alt", "src", "href"
                             ];
    MonkeyTalk.keys = ["monkeyId", "id", "name", "value", "title",
                       "className", "textContent"
                       ];
    // MonkeyTalk.keys =
    // ["monkeyId","id","name","value","title","className","alt","textContent","src","href"];
    
    MonkeyTalk.isIos = function () {
        var userAgent = window.navigator.userAgent;
        
        if (userAgent.match(/iPad/i) || userAgent.match(/iPhone/i)) {
            return true;
        }
        
        return false;
    };
    
    MonkeyTalk.enterTextAtPoint = function (x,y,value) {
    	var element = document.elementFromPoint(x, y);
        element.value = value;
        
        if (element.onchange != undefined) {
        	element.onchange();
        }
    };
    
    MonkeyTalk.scroll = function(xpath, left, top) {
    	var element = MonkeyTalk.getHtmlElementFromXpathWithOrdinal(xpath, 1);
    	console.log("element: " + element);
    	console.log("t: " + top);
    	console.log("l: " + left);
    	element.scrollLeft = left;
    	element.scrollTop = top;
    }
    
    MonkeyTalk.monkeyIdMatchesElement = function (monkeyId, monkeyElement) {
        var keys = MonkeyTalk.keys;
        
        for (var i = 0; i < keys.length; i++) {
            var key = keys[i];
            if (monkeyId == monkeyElement[key])
                return true;
        }
        
        return false;
    };
    
    MonkeyTalk.ordinalFromMonkeyId = function (monkeyId) {
        var regex = new RegExp("^#[0-9]+(?:\\.[0-9]*)?$");
        var ordinal = regex.exec(monkeyId);
        
        if (ordinal != undefined && ordinal.length > 0) {
            ordinal = monkeyId.replace("#", "");
            
            return parseInt(ordinal);
        } else if (monkeyId == "*") {
            return 1;
        }
        
        return null;
    };
    
    MonkeyTalk.getElementByMonkeyIndex = function (element, monkeyElement, monkeyId, matches) {
        var regex = new RegExp("\\([0-9]+(?:\\.[0-9]*)?\\)$");
        var monkeyIndex = regex.exec(monkeyId);
        
        if (monkeyIndex != undefined && monkeyIndex.length > 0) {
            var ordinalRegex = new RegExp("[0-9]+(?:\\.[0-9]*)?");
            var mid = monkeyId.replace(monkeyIndex, "");
            monkeyIndex = ordinalRegex.exec(monkeyIndex);
            
            for (var j = 0; j < MonkeyTalk.finderKeys.length; j++) {
                var key = MonkeyTalk.finderKeys[j];
                var match = "";
                
                if (key == "textContent") {
                    match = element.textContent;
                } else {
                    match = element.getAttribute(key);
                }
                
                if (mid == match) {
                    matches[matches.length] = match;
                    
                    if (matches.length == parseInt(monkeyIndex)) {
                        return JSON.stringify(monkeyElement);
                    }
                }
            }
        }
        
        return null;
    };
    
    MonkeyTalk.getHtmlElementFromXpathWithOrdinal = function (expression, ordinal) {
        wgxpath.install();
        var docElement = MonkeyTalk.getElementFromXpathWithOrdinalFromDoc(expression,ordinal,document);
        
		if (docElement == null) {
			// iframe support
    		var mtframes = document.getElementsByTagName('iframe');
    
    		for (var i = 0; i < mtframes.length; i++) {
    			try {
    				var mtframe = mtframes[i];
    		    	var mtdoc = (mtframe.contentWindow || mtframe.contentDocument);
    		    	if (mtdoc.document) {
      		    	  mtdoc = mtdoc.document;
      		    	  docElement = MonkeyTalk.getElementFromXpathWithOrdinalFromDoc(expression,ordinal,mtdoc);
      		      
      		    	  if (docElement) return docElement;
      		    	}
    			} catch (ex) {
    				// Uncaught SecurityError?
    			}
   		 	}
		}
        return docElement;
    };
    
    MonkeyTalk.getElementFromXpathWithOrdinal = function (expression, ordinal) {
    	var htmlElement = MonkeyTalk.getHtmlElementFromXpathWithOrdinal(expression, ordinal);
    	
    	if (htmlElement == null) {
    		return null;
    	}
    	
        var monkeyElement = MonkeyTalk.monkeyElement(htmlElement);
        return JSON.stringify(monkeyElement);
    };
    
    MonkeyTalk.getElementFromXpathWithOrdinalFromDoc = function (expression, ordinal,doc) {
        wgxpath.install();
        var xpath = doc.evaluate(expression, doc, null, XPathResult.ANY_TYPE, null);
        if (xpath != undefined && xpath != null) {
            var element = null;
            for (i = 0; i < ordinal; i++) {
                console.log('iterating: ' + i);
                element = xpath.iterateNext();
            }
            
            if (element == null) {
                return null;
            }
            
            return element;
        }
        return null;
    };
    
    MonkeyTalk.getElementFromXpath = function (expression) {
        wgxpath.install();
        var xpath = document.evaluate(expression, document, null, XPathResult.ANY_TYPE, null);
        if (xpath != undefined && xpath != null) {
            var element = xpath.iterateNext();
            
            if (element == null) {
                return null;
            }
            
            var monkeyElement = MonkeyTalk.monkeyElement(element);
            return JSON.stringify(monkeyElement);
        }
        return null;
    };
    
    MonkeyTalk.getElementsFromXpath = function (expression) {
        wgxpath.install();
        var xpath = document.evaluate(expression, document, null, XPathResult.ANY_TYPE, null);
        if (xpath != undefined && xpath != null) {
            var elementValues = new Array();
            var element = xpath.iterateNext();
            
            var i = 0;
            while (element != null) {
                var adapter = adapters.getAdapterForElement(element);
                var monkeyElement = MonkeyTalk.monkeyElement(element);
                
                if (monkeyElement != null) {
                	elementValues[i] = monkeyElement;
                	i++;
                }
                
                element = xpath.iterateNext();
            }
            
            return JSON.stringify(elementValues);
        }
        return null;
    };
    
    MonkeyTalk.getElement = function (elements, monkeyId, componentType) {
        console.log("elements: " + elements);
        var monkeyElements = new Array();
        var matches = new Array();
        var ordinal = MonkeyTalk.ordinalFromMonkeyId(monkeyId);
        var xpathRegex = new RegExp("^xpath=");
        var xpathPrefix = xpathRegex.exec(monkeyId);
        
        if (xpathPrefix != undefined && xpathPrefix.length > 0) {
            var expression = monkeyId.replace(xpathPrefix, "");
            return MonkeyTalk.getElementFromXpath(expression);
        }
        
        for (var i = 0; i < elements.length; i++) {
            var element = elements[i];
            var monkeyElement = MonkeyTalk.monkeyElement(element);
            var monkeyIndexElement = MonkeyTalk.getElementByMonkeyIndex(element, monkeyElement, monkeyId, matches);
            
            monkeyElements[i] = monkeyElement;
            console.log("checking: " + monkeyElement.monkeyId + " for: " + monkeyId);
            
            if (monkeyIndexElement != null) {
                return monkeyIndexElement;
            } else if (MonkeyTalk.monkeyIdMatchesElement(monkeyId, monkeyElement)) {
                return JSON.stringify(monkeyElement);
            } else if (ordinal != null) {
                var ordinalMatch = ordinal == 1 ? '*' : '#' + ordinal;
                
                if ((componentType == monkeyElement["component"]) &&
                    (ordinalMatch == monkeyElement["ordinal"])) {
                    return JSON.stringify(monkeyElement);
                }
            }
        }
        
        return null;
    };
    
    MonkeyTalk.getHtmlElement = function (elements, monkeyId) {
        var monkeyElements = new Array();
        
        for (var i = 0; i < elements.length; i++) {
            var element = elements[i];
            var monkeyElement = MonkeyTalk.monkeyElement(element);
            
            monkeyElements[i] = monkeyElement;
            
            if (MonkeyTalk.monkeyIdMatchesElement(monkeyId, monkeyElement)) {
                return element;
            }
        }
        
        return null;
    };
    
    /**
     * Return list of all descendants
     */
    MonkeyTalk.getAllElements = function (root) {
        console.log("Getting all elems");
        var resultList = new Array();
        MonkeyTalk._getAllElements(resultList, root);
        return resultList;
        
    };
    
    MonkeyTalk._getAllElements = function (resultList, root) {
        
        if (root.tagName) {
            resultList.push(root);
        }
        for (var i = 0; i < root.childNodes.length; i++) {
            MonkeyTalk._getAllElements(resultList, root.childNodes.item(i));
            
        }
    };
    
    MonkeyTalk.getCell = function (tableElements, tableId, cellId) {
        var table = MonkeyTalk.getHtmlElement(tableElements, tableId);
        var rows = table.getElementsByTagName('tr');
        for (var i = 0; i < rows.length; i++) {
            var row = rows[i];
            var cells = row.cells;
            for (var j = 0; j < cells.length; j++) {
                var cell = cells[j];
                var monkeyElement = MonkeyTalk.monkeyElement(cell);
                if (MonkeyTalk.monkeyIdMatchesElement(cellId, monkeyElement)) {
                    return JSON.stringify(monkeyElement);
                }
            }
        }
        
        return null;
    };
    
    MonkeyTalk.getRadioButton = function (radiobuttons, value) {
        console.log('radio count: ' + radiobuttons.length);
        console.log('value: ' + value);
        for (var i = 0; i < radiobuttons.length; i++) {
            var element = radiobuttons[i];
            var monkeyElement = MonkeyTalk.monkeyElement(element);
            
            if (value == element.value || value == element.getAttribute('id')) {
                console.log('json: ' + JSON.stringify(monkeyElement));
                return JSON.stringify(monkeyElement);
            }
        }
        
        return null;
    };
    
    MonkeyTalk.getNthElement = function (elements, n, componentType) {
        var limitedElements = new Array();
        var offset = n - 1;
        
        for (var i = 0; i < elements.length; i++) {
            var e = elements[i];
            if (e.type != 'hidden')
                limitedElements[limitedElements.length] = e;
        }
        
        if (offset < 0)
            offset = 0;
        
        if (limitedElements.length > offset) {
            var element = limitedElements[offset];
            
            var monkeyElement = MonkeyTalk.monkeyElement(element);
            
            console.log('type: ' + monkeyElement["component"]);
            
            if (componentType == monkeyElement["component"]) {
                var json = JSON.stringify(monkeyElement);
                return json;
            }
        }
        
        return null;
    };
    
    MonkeyTalk.parentIframeLocation = function (element) {
    	if (element.ownerDocument === document) {
    		return {x:0,y:0};
    	}
    
    	var arrFrames = document.getElementsByTagName('iframe');
        var frame = null;
        for (var i = 0; i < arrFrames.length; i++) {
            if (arrFrames[i].contentWindow.document === element.ownerDocument) {
            	frame = arrFrames[i];
            	break;
            }
        }
        if (frame == null) return {x:0,y:0};
        return {x:frame.offsetLeft,y:frame.offsetTop};
    };
    
    MonkeyTalk.monkeyElement = function (element) {
        var monkeyElement = {};
        var adapter = adapters.getAdapterForElement(element);
        var mtc = adapter.getMTCommand(element, "tap");
        var ordinal = adapter.ordinal(element);
        // console.log('element: ' + mtc.monkeyId);
        
        var elementAttributes = element.attributes;
        
        for (var i = 0; i < elementAttributes.length; i++) {
            var attributeName = elementAttributes[i].name;
            var attributeValue = elementAttributes[i].value;
            
            monkeyElement[attributeName] = attributeValue;
        }
        
        monkeyElement["monkeyId"] = mtc.monkeyId;
        monkeyElement["component"] = mtc.componentType;
        
        var rect = element.getBoundingClientRect();
        var x = rect.left;
        var y = rect.top;
        
        // adjust x and y if element is inside an iframe
        var parentIframeLocation = MonkeyTalk.parentIframeLocation(element);
        x += parentIframeLocation["x"];
        y += parentIframeLocation["y"];
        
        monkeyElement["id"] = element.id != undefined ? element.id : null;
        monkeyElement["name"] = element.name != undefined ? element.name : null;
        monkeyElement["tagName"] = element.tagName != undefined ? element.tagName : null;
        monkeyElement["className"] = element.className != undefined ? element.className : null;
        monkeyElement["value"] = element.value != undefined ? element.value : null;
        monkeyElement["textContent"] = element.textContent != undefined ? element.textContent : null;
        monkeyElement["type"] = element.type != undefined ? element.type : null;
        monkeyElement["x"] = parseInt(x);
        monkeyElement["y"] = parseInt(y);
        monkeyElement["width"] = parseInt(element.clientWidth);
        monkeyElement["height"] = parseInt(element.clientHeight);
        monkeyElement["title"] = element.title != undefined ? element.title : null;
        monkeyElement["component"] = mtc.componentType != undefined ? mtc.componentType : "View";
        monkeyElement["ordinal"] = ordinal != undefined ? ordinal : null;
        monkeyElement["checked"] = element.checked != undefined && element.checked == true ? "true" : "false";
        
        if (element.type != undefined && element.type == "radio") {
            if (element.name != undefined) {
                var checkedElement = document.querySelector('input[name="' + element.name + '"]:checked');
                
                if (checkedElement != null) {
                    var selectedValue = checkedElement.value;
                    
                    if (selectedValue == '-1') {
                        selectedValue = checkedElement.getAttribute('id');
                    }
                    
                    monkeyElement["selected"] = selectedValue;
                }
            }
        }
        
        return monkeyElement;
    };
    
    // ------------------------------------------------------
    // COMPONENT TREE
    // ------------------------------------------------------
    
    // ------------------------------------------------------
    // Return component tree json
    // ------------------------------------------------------
    MonkeyTalk.getComponentTreeJson = function() {
		console.log("fetching component tree");
    	wgxpath.install();
    	var xpath = document.evaluate( "/", document, null, XPathResult.ANY_TYPE, null );
		if (xpath != undefined && xpath != null) {
			var rootNode = xpath.iterateNext();
			if (rootNode != null && rootNode != undefined) {
				return JSON.stringify(MonkeyTalk.getComponentTreeJson2(rootNode));
			}
		}
		return null;
	}
	
	// ------------------------------------------------------
	// Return component tree json for supplied element
	// ------------------------------------------------------
	MonkeyTalk.getComponentTreeJson2 = function(element) {
		// console.log("getComponentTreeJson2(element) ==> element=" + element + " with tagName=" + element.tagName);
		var nodeItems = new Array();
		if (element != null && element != undefined) {
			var childNodes = element.childNodes; // node list
			if (childNodes != undefined && childNodes != null) {
				for (var i=0; i<childNodes.length; i++) {
					var node = childNodes.item(i);
					if (node != undefined && node != null) {
						var nodeType = node.nodeType;
						if (nodeType==1) {
							// it's an element
							if (node == element) {
								// in case of self-child links
								continue;
							}
							
							try {
								if (node.tagName.toLowerCase() != "script" && node.tagName.toLowerCase() != "head") {
									// ignore script elements
									nodeItems[nodeItems.length]=MonkeyTalk.getComponentTreeEntry(node);
								}
							} catch (err) {
								// ignore
							}
						}
					}
				}
			}
		}
        return nodeItems;
	}

	// ------------------------------------------------------
	// Return component tree item for the supplied element
	// ------------------------------------------------------
	MonkeyTalk.getComponentTreeEntry = function(element) {
		
		var comp = new Object();
		comp["ComponentType"] = "View";

		var identifyingValues = MonkeyTalk.getIdentifyingValues(element);
		var tagAdapter = adapters.getAdapterForElement(element);
		if (tagAdapter != undefined && tagAdapter!= null) {
			comp["monkeyId"] = tagAdapter.mid;
			comp["ComponentType"] = tagAdapter.componentType;
		}
		
		var match = /\r|\n/.exec(comp.monkeyId);
			if (comp.monkeyId != undefined && match) {
    			comp.monkeyId = tagAdapter.ordinal(element);
			}
		
		if (identifyingValues != undefined && identifyingValues != null && identifyingValues.length>0) {
			comp["identifiers"] = identifyingValues;
			if (comp.monkeyId == undefined || comp.monkeyId == null || match) {
				comp["monkeyId"] = identifyingValues[0];
			}
		}
		
		comp["className"] = element.tagName; 
		// comp["visible"] = element.isVisible;
		comp["ordinal"] = MonkeyTalk.ordinalFromMonkeyId(comp.monkeyId);
		comp["children"] = MonkeyTalk.getComponentTreeJson2(element);
		
		return comp;
	}
    
    MonkeyTalk.getIdentifyingValues = function (element) {
        var keys = MonkeyTalk.keys;
        var identifyingValues = new Array();
        for (var i = 0; i < keys.length; i++) {
            var key = keys[i];
            if (key == "textContent") {
                continue;
            }
            if (element.hasOwnProperty(key)) {
                var val = element[key];
                if (val != undefined && val != null && val.length > 0) {
                    identifyingValues[identifyingValues.length] = val;
                }
            }
        }
        return identifyingValues;
    };
    
    // ------------------------------------------------------
    // RECORDER
    // ------------------------------------------------------
    
    MonkeyTalk.record = function (event, action) {
        element = adapters.recordElement(event);
        MonkeyTalk.recordWithElement(element, action, null);
    };
    
    MonkeyTalk.recordScroll = function (element) {
 		while (element != null) {
 			if (element.scrollTop > 0) {
 				var args = new Array();
 				args[0] = element.scrollLeft;
 				args[1] = element.scrollTop;
 				MonkeyTalk.recordWithElement(element, "scroll", args);
 				break;
 			}
 			element = element.parentNode;
 		}
    };
    
    MonkeyTalk.recordWithElement = function (element, action, args) {
        console.log('MonkeyTalk Recording ' + action);
        var adapter = adapters.getAdapter(event);
        var mtc = adapter.getMTCommand(element, action);
        
        if (mtc.shouldIgnore == true)
            return;
            
        if (action == null || action == 'change') {
            action = mtc.action;
        }
        
        if (args == null) {
        	args = mtc.args;
        }
        
        var json = {};
        json["component"] = mtc.componentType;
        json["monkeyId"] = mtc.monkeyId;
        json["action"] = action;
        json["args"] = args;
        
        var jsonString = JSON.stringify(json);
        
        if (MonkeyTalk.isIos() == true) {
            // need to make this json
            xPathResult = 'ComponentType*' + mtc.componentType + ';MonkeyId*' + mtc.monkeyId + ';Action*' + action + ';Args*' + mtc.args;
            sendToObjCLib(element.tagName, xPathResult);
        } else {
            alert('mtrecorder:' + jsonString);
            // window.mtrecorder.recordJson(jsonString);
        }
    };
    
    MonkeyTalk.recordTap = function (x, y) {
        var action = 'Tap';
        console.log('MonkeyTalk Recording Tap');
        
        element = adapters.recordFromElement(document.elementFromPoint(x, y));
        
        if (element.tagName.toLowerCase() == 'iframe')
            element = adapters.elementInIframeAtPoint(element, x, y);
        
        var adapter = adapters.getAdapterForElement(element);
        var mtc = adapter.getMTCommand(element, action);
        var mtaction = action;
        
        if (mtc.shouldIgnore == true) return;
        
        if (mtaction == null || mtaction == 'change' || mtc.action != null)
            mtaction = mtc.action;
        
        console.log('Element TagName:' + element.tagName);
        
        if (mtc.componentType == undefined)
            mtc.componentType = 'View';
        
        var json = {};
        json["component"] = mtc.componentType;
        json["monkeyId"] = mtc.monkeyId;
        json["action"] = mtaction;
        json["args"] = mtc.args;
        
        var jsonString = JSON.stringify(json);
        
        if (MonkeyTalk.isIos() == true) {
            return jsonString;
        } else {
            //window.mtrecorder.recordJson(jsonString);
            console.log('mtrecorder:' + jsonString);
        }
        
        console.log('component: ' + mtc.componentType + ' monkeyId: ' + mtc.monkeyId + ' action: ' + mtaction + ' args: ' + mtc.args);
        //console.log ('{' + ''component':' + ''' + mtc.componentType + ''' + ',' + ''monkeyId':' + ''' + mtc.monkeyId + ''' + ',' + ''action':' + ''' + mtaction + ''' + ',' + ''args':' + ''' + mtc.args + ''' + '}');
    };
    
    // ------------------------------------------------------
    // SEND TO OBJC
    // ------------------------------------------------------
    sendToObjCLib = function (key, val) {
        var iframe = document.createElement('IFRAME');
        iframe.setAttribute('src', key + ':monkeytalk' + val);
        document.documentElement.appendChild(iframe);
        iframe.parentNode.removeChild(iframe);
        iframe = null;
    };
    
    function MonkeyElement() {};
    var monkey = new MonkeyElement();
    
    // ------------------------------------------------------
    // MT COMMAND
    // ------------------------------------------------------
    var mt = {};
    mt.command = function (componentType, monkeyId, action, args) {
        this.componentType = componentType;
        this.monkeyId = monkeyId;
        this.action = action;
        this.args = args;
        this.shouldIgnore = false;
    };
    
    // ------------------------------------------------------
    // ADAPTERS
    // ------------------------------------------------------
    var adapters = {};
    
    // get the adapter for each element
    adapters.getAdapter = function (event) {
        element = adapters.recordElement(event);
        var tagName = element.tagName.toLowerCase();
        //console.log('element tag: ' + tagName);
        if (adapters[tagName]) {
            return new adapters[tagName](element);
        }
        
        return new adapters.tag(element);
    };
    
    // get the adapter for specific element
    adapters.getAdapterForElement = function (element) {
        var tagName = element.tagName.toLowerCase();
        //console.log('element tag: ' + tagName);
        if (adapters[tagName]) {
            return new adapters[tagName](element);
        }
        
        return new adapters.tag(element);
    };
    
    // get the element we want to record
    adapters.recordElement = function (event) {
        element = event.target || event.currentTarget || event.srcElement;
        
        return adapters.recordFromElement(element);
    };
    
    // get the element we want to record
    adapters.recordFromElement = function (element) {
        if (element.parentNode.tagName == undefined)
            return element;
        
        if (element.parentNode.tagName.toString().toLowerCase() == 'button') {
            console.log('Using Parent Tag');
            return element.parentNode;
        }
        
        return element;
    };
    
    adapters.elementInIframeAtPoint = function (frame, x, y) {
        var rect = frame.getBoundingClientRect();
        x -= rect.left;
        y -= rect.top;
        var doc = (frame.contentWindow || frame.contentDocument);
        if (doc.document)
            doc = doc.document;
        
        var element = adapters.recordFromElement(doc.elementFromPoint(x, y));
        
        return element;
    };
    
    // ------------------------------------------------------
    // DEFAULT ADAPTER
    // ------------------------------------------------------
    adapters.tag = function (element) {
        if (!element) {
            return;
        }
        
        this.element = element;
        this.componentType = this.componentNames[element.tagName.toLowerCase()];
        this.monkeyId = this.mid(this.element, true);
    };
    
    // monkeyID
    adapters.tag.prototype.mid = function (e, findOrdinal) {
        var monkeyId;
        
        // do not record value as monkeyId for
        // ItemSelector, CheckBox or RadioButtons
        if (this.componentType != undefined &&
            this.componentType.toLowerCase() == 'table') {
            monkeyId = e.id || e.name || e.value || e.title || e.styleClass;
        }
        if (e.type == undefined) {
            monkeyId = e.getAttribute('id') || e.getAttribute('name') || e.getAttribute('value') || e.getAttribute('title') || e.getAttribute('class') || e.getAttribute('alt') || e.textContent || e.getAttribute('src') || e.getAttribute('href');
        } else if (e.type.toLowerCase() == 'radio') {
            monkeyId = e.name;
        } else if (e.tagName.toLowerCase() == 'select' || e.tagName.toLowerCase() == 'textarea' || e.tagName.toLowerCase() == 'input') {
            if (e.type.toLowerCase() != 'submit' || e.type.toLowerCase() != 'reset')
                monkeyId = e.id || e.name || e.title || e.getAttribute('class') || e.styleClass;
            else {
                monkeyId = e.id || e.name || e.value || e.textContent || e.title || e.getAttribute('class') || e.styleClass;
            }
        } else {
            monkeyId = e.getAttribute('id') || e.getAttribute('name') || e.getAttribute('value') || e.getAttribute('title') || e.getAttribute('class') || e.getAttribute('alt') || e.textContent || e.getAttribute('src') || e.getAttribute('href');
        }
        
        if (monkeyId == e.textContent) {
            var isTextContentLegal = monkeyId.substring(0, 1) == ' ';
            isTextContentLegal = isTextContentLegal || monkeyId.substring(monkeyId.length - 1, monkeyId.length) == ' ';
            isTextContentLegal = isTextContentLegal || (this.element.textContent == monkeyId && this.element.innerHTML.indexOf('<') !== -1);
            
            //if (!isTextContentLegal)
        }
        
        if (monkeyId == undefined)
            return null;
        
        // do not use ordinal mid for radiobuttons
        if (findOrdinal && this.componentType != undefined && this.componentType.toLowerCase() != 'radiobuttons')
            monkeyId = this.ordinalMid(e, monkeyId);
        
        return monkeyId;
    };
    
    // get ordinal monkey ID
    adapters.tag.prototype.ordinalMid = function (e, monkeyId) {
        // find all elements in the DOM with element e tag
        var elements = document.getElementsByTagName(e.tagName);
        var ordinalMid = 0;
        
        for (var i = 0; i < elements.length; i++) {
            var element = elements[i];
            var elementMid = this.mid(element, false);
            
            if (monkeyId == elementMid) {
                //console.log('elementMid: ' + e.textContent + ' current: ' + element.textContent + ' ordinal: ' + i);
                if (element == e) {
                    //console.log('found element');
                    i = ordinalMid;
                    break;
                } else
                    ordinalMid++;
            }
            
        }
        
        // increment to make 1 based
        ordinalMid++;
        
        if (ordinalMid > 1)
            return monkeyId + '(' + ordinalMid + ')';
        
        return monkeyId;
    };
    
    adapters.tag.prototype.ordinal = function (e) {
        // Find all elements in the DOM
        var elements = document.getElementsByTagName('*');
        var ordinal = 0;
        
        for (var i = 0; i < elements.length; i++) {
            var element = elements[i];
            var isElementRadio = element.type == 'radio';
            var isElementCheckBox = element.type == 'checkbox';
            var isInput = (e.tagName == 'input' && (e.type == undefined || e.type == 'text'));
            
            var isTagMatch = (element.tagName.toLowerCase() == e.tagName.toLowerCase());
            var isTypeMatch = (element.type == e.type);
            
            if (element == e) {
                i = elements.length;
            } else if (isTagMatch && isTypeMatch) {
                // Increment input only if it is not a checkbox or radio
                if (isInput && !isElementRadio && !isElementCheckBox) {
                    ordinal++;
                    //log('ordinal: ' + element.tagName + ' type: ' + element.type + ' ordinal: ' + ordinal + ' element: ' + e.type);
                } else if (!isInput || isElementRadio || isElementCheckBox) {
                    ordinal++;
                }
            }
        }
        
        // Increment to make 1 based
        ordinal++;
        
        if (ordinal == 1)
            return '*';
        
        return '#' + ordinal.toString();
    };
    
    adapters.tag.prototype.getMTCommand = function (element, action) {
        var componentType = this.componentNames[element.tagName.toLowerCase()];
        var monkeyId = this.mid(element, true);
        var shouldFindOrdinal = !monkeyId;
        shouldFindOrdinal = shouldFindOrdinal || monkeyId.substring(0, 1) == ' ';
        shouldFindOrdinal = shouldFindOrdinal || monkeyId.substring(monkeyId.length - 1, monkeyId.length) == ' ';
        shouldFindOrdinal = shouldFindOrdinal || (element.textContent == monkeyId && element.innerHTML.indexOf('<') !== -1);
        
        if (shouldFindOrdinal)
            monkeyId = this.ordinal(this.element);
        
        var action = this.actionTypes[element.tagName.toString().toLowerCase()];
        var args = '';
        return new mt.command(componentType, monkeyId, action, args);
    };
    
    // ------------------------------------------------------
    // COMPONENT TYPES
    // ------------------------------------------------------
    adapters.tag.prototype.componentNames = {
    a: 'Link',
    button: 'Button',
    select: 'ItemSelector',
    table: 'Table',
    td: 'Table',
    th: 'Table',
    textarea: 'TextArea',
    input: 'Input',
    span: 'View',
    div: 'View',
    img: 'Image',
    textbox: 'TextArea',
    toolbarbutton: 'Browser',
    checkbox: 'CheckBox',
    radio: 'RadioButtons',
    text: 'Input',
    h1: 'Label',
    h2: 'Label',
    h3: 'Label',
    h4: 'Label',
    h5: 'Label',
    h6: 'Label',
    p: 'Label'
    };
    
    // ------------------------------------------------------
    // ACTION TYPES
    // ------------------------------------------------------
    adapters.tag.prototype.actionTypes = {
    a: 'tap',
    button: 'tap',
    span: 'tap',
    text: 'tap',
    label: 'tap',
    div: 'tap',
    img: 'tap',
    select: 'select',
    radio: 'select',
    h1: 'tap',
    h2: 'tap',
    h3: 'tap',
    h4: 'tap',
    h5: 'tap',
    h6: 'tap'
    };
    
    // ------------------------------------------------------
    // INPUT ADAPTER
    // ------------------------------------------------------
    adapters.input = function (element) {
        adapters.tag.call(this, element);
        if (this.element.type == undefined || this.element.type.length==0) {
        	this.componentType = this.componentNames["text"];
        } else {
        	this.componentType = this.componentNames[this.element.type.toLowerCase()];
        }
    };
    
    adapters.input.prototype = new adapters.tag;
    
    adapters.input.prototype.getMTCommand = function (element, action) {
        var mtcommand = adapters.tag.prototype.getMTCommand.call(this, element, action);
        if (element.type.toLowerCase() == 'radio') {
            if (action.toLowerCase() != 'change') {
                mtcommand.shouldIgnore = true;
                return mtcommand;
            }
            mtcommand.action = 'select';
            mtcommand.componentType = 'RadioButtons';
            mtcommand.monkeyId = adapters.tag.prototype.mid.call(this, element, true);
            var value = element.value;
            
            if (value == '-1') {
                value = element.getAttribute('id');
            }
            
            mtcommand.args = value;
        } else if (element.type.toLowerCase() == 'checkbox') {
            if (action.toLowerCase() != 'change') {
                mtcommand.shouldIgnore = true;
                return mtcommand;
            }
            
            var checkbox = this.element;
            if (checkbox.checked)
                mtcommand.action = 'on';
            else
                mtcommand.action = 'off';
            mtcommand.componentType = 'CheckBox';
        } else if (element.type.toLowerCase() == 'button') {
            mtcommand.componentType = 'Button';
            mtcommand.monkeyId = element.value;
        } else {
            if (action != null && action.toLowerCase() == 'change') {
                // should not ignore this
                // need to fix keyup record to take multiple args
                mtcommand.shouldIgnore = true;
                mtcommand.action = 'enterText';
                mtcommand.args = element.value + ',enter';
                return mtcommand;
            }
            console.log('value: ' + element.value);
            mtcommand.args = element.value;
        }
        return mtcommand;
    };
    
    // ------------------------------------------------------
    // SELECT ADAPTER
    // ------------------------------------------------------
    adapters.select = function (element) {
        adapters.tag.call(this, element);
        this.componentType = this.componentNames[this.element.type.toLowerCase()];
    };
    
    adapters.select.prototype = new adapters.tag;
    
    adapters.select.prototype.getMTCommand = function (element, action) {
        var mtcommand = adapters.tag.prototype.getMTCommand.call(this, element, action);
        
        if (action.toLowerCase() != 'change') {
            mtcommand.shouldIgnore = true;
            return mtcommand;
        }
        
        mtcommand.args = element.value;
        
        return mtcommand;
    };
    
    // ------------------------------------------------------
    // TEXTAREA ADAPTER
    // ------------------------------------------------------
    adapters.textarea = function (element) {
        adapters.tag.call(this, element);
        this.componentType = this.componentNames[this.element.type.toLowerCase()];
    };
    
    adapters.textarea.prototype = new adapters.tag;
    
    adapters.textarea.prototype.getMTCommand = function (element, action) {
        var mtcommand = adapters.tag.prototype.getMTCommand.call(this, element, action);
        
        if (action != null && action.toLowerCase() == 'change') {
            // should not ignore this
            // need to fix keyup record to take multiple args
            mtcommand.shouldIgnore = true;
            mtcommand.action = 'enterText';
            mtcommand.args = element.value + ',enter';
            return mtcommand;
        }
        
        mtcommand.args = element.value;
        
        return mtcommand;
    };
    
    // ------------------------------------------------------
    // TABLE ADAPTER
    // ------------------------------------------------------
    adapters.table = function (element) {
        adapters.tag.call(this, element);
    };
    
    adapters.table.prototype = new adapters.tag;
    
    adapters.table.prototype.getMTCommand = function (command, target, value) {
        var mtcommand = adapters.tag.prototype.getMTCommand.call(this, command, target, value);
        var child = this.element;
        var table = child;
        var tr = null;
        var cell = null;
        
        while (child.tagName.toLowerCase() != "table") {
            if (child.tagName.toLowerCase() == "tr")
                tr = child;
            else if (child.tagName.toLowerCase() == "td" ||
                     child.tagName.toLowerCase() == "th")
                cell = child;
            
            child = child.parentNode;
            table = child;
        }
        
        if (!tr) {
            console.log('tr not found');
            return mtcommand;
        }
        
        console.log('tr found: ' + tr.textContent);
        
        // Set monkeyId to that of table
        // to avoid recording monkeyId of tr, td, etc. monkeyIds
        mtcommand.monkeyId = adapters.tag.prototype.mid.call(this, table, true);
        
        if (!mtcommand.monkeyId)
            mtcommand.monkeyId = adapters.tag.prototype.ordinal.call(this, table);
        
        if (tr.textContent.length > 0) {
            console.log('set action select');
            mtcommand.action = 'select';
            mtcommand.args = tr.textContent;
        } else {
            var row = tr.rowIndex + 1;
            mtcommand.action = 'selectIndex';
            
            if (cell) {
                var column = Array.prototype.indexOf.call(tr.childNodes, cell);
                
                if (column == 0)
                    column++;
                
                mtcommand.args = row + "','" + column;
            } else {
                mtcommand.args = row;
            }
        }
        
        return mtcommand;
    };
    
    // ------------------------------------------------------
    // TR ADAPTER
    // ------------------------------------------------------
    adapters.tr = function (element) {
        adapters.table.call(this, element);
    };
    
    adapters.tr.prototype = new adapters.table;
    
    // ------------------------------------------------------
    // TD ADAPTER
    // ------------------------------------------------------
    adapters.td = function (element) {
        adapters.table.call(this, element);
    };
    
    adapters.td.prototype = new adapters.table;
    
    // ------------------------------------------------------
    // TH ADAPTER
    // ------------------------------------------------------
    adapters.th = function (element) {
        adapters.table.call(this, element);
    };
    
    adapters.th.prototype = new adapters.table;
    
    // ------------------------------------------------------
    // SETUP DOCUMENT
    // ------------------------------------------------------
    // add listeners for elements in iframes
    var mtframes = document.getElementsByTagName('iframe');
    
    for (var i = 0; i < mtframes.length; i++) {
    	try {
    		var mtframe = mtframes[i];
        	//console.log('element: ' + mtframe.tagName + ':' + mtframe.getAttribute('id'));
        
        	var mtdoc = (mtframe.contentWindow || mtframe.contentDocument);
        	if (mtdoc.document)
        	    mtdoc = mtdoc.document;
        
        	mtdoc.onkeyup = function (event) {
            MonkeyTalk.record(event, 'enterText')
        	};
        	mtdoc.onchange = function (event) {
            MonkeyTalk.record(event, 'change')
        	};
        	mtdoc.onclick = function (event) {
        	    if (!MonkeyTalk.didHandleTap) {
            		MonkeyTalk.record(event, 'tap');
            		MonkeyTalk.didHandleTap = false;
        		}
        	};
    	} catch (ex) {
    		// Uncaught SecurityError?
    	}
    }
    
    // handle keyup and change events via js
    // taps handled in objc from gestures
    document.onkeyup = function (event) {
        MonkeyTalk.record(event, 'enterText')
    };
    document.onchange = function (event) {
        MonkeyTalk.record(event, 'change')
    };
    
    document.addEventListener('touchstart', function(e){

 	}, false);
 	
 	document.addEventListener('touchend', function(e){
 		element = adapters.recordElement(e);
 		MonkeyTalk.recordScroll(element);
 	}, false);
    
    if (MonkeyTalk.isIos() != true) {
        document.onclick = function (event) {
        	if (!MonkeyTalk.didHandleTap) {
            	MonkeyTalk.record(event, 'tap');
            	MonkeyTalk.didHandleTap = false;
            }
        };
    }
}
