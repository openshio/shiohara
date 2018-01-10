shioharaApp.factory('shWidgetResource', [ '$resource', 'shAPIServerService', function($resource, shAPIServerService) {
	return $resource(shAPIServerService.get().concat('/widget/:id'), {
		id : '@id'
	}, {
		update : {
			method : 'PUT'
		}
	});
} ]);