import {TestBed} from '@angular/core/testing';

import {ShareResourceService} from './share-resource.service';

describe('ShareResourceService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ShareResourceService = TestBed.get(ShareResourceService);
    expect(service).toBeTruthy();
  });
});
